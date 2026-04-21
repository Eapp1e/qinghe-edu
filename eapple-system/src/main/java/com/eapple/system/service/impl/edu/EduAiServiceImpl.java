package com.eapple.system.service.impl.edu;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.config.EduAiProperties;
import com.eapple.system.domain.SysConfig;
import com.eapple.system.domain.edu.EduAiLog;
import com.eapple.system.domain.edu.EduAiModelOption;
import com.eapple.system.mapper.SysConfigMapper;
import com.eapple.system.service.edu.IEduAiLogService;
import com.eapple.system.service.edu.IEduAiService;

@Service
public class EduAiServiceImpl implements IEduAiService
{
    private static final String USER_MODEL_KEY_PREFIX = "edu.ai.userModel.";

    private static final List<EduAiModelOption> AVAILABLE_MODELS = List.of(
            new EduAiModelOption("Qwen/Qwen2.5-7B-Instruct", "Qwen 2.5 7B", "已验证可用，响应更快，适合日常教学与学习辅助"),
            new EduAiModelOption("deepseek-ai/DeepSeek-V3", "DeepSeek V3", "已验证可用，生成质量更高，适合正式内容与完整建议"));

    @Autowired
    private EduAiProperties aiProperties;

    @Autowired
    private IEduAiLogService aiLogService;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public String answerHomeworkQuestion(Long questionId, String prompt)
    {
        return invoke("homework_answer", questionId, buildHomeworkPrompt(prompt));
    }

    @Override
    public String generateCourseNotice(Long courseId, String prompt)
    {
        return invoke("course_notice", courseId, buildNoticePrompt(prompt));
    }

    @Override
    public String generateTeachingSuggestion(Long courseId, String prompt)
    {
        return invoke("teaching_suggestion", courseId, buildTeachingPrompt(prompt));
    }

    @Override
    public String generateCourseRecommendation(Long studentUserId, String prompt)
    {
        return invoke("course_recommendation", studentUserId, buildRecommendationPrompt(prompt));
    }

    @Override
    public String generateOnlineResourceRecommendation(Long userId, String prompt)
    {
        return invoke("online_resource_recommendation", userId, buildOnlineResourcePrompt(prompt));
    }

    @Override
    public String getCurrentModel()
    {
        return resolveCurrentModel();
    }

    @Override
    public List<EduAiModelOption> getAvailableModels()
    {
        return AVAILABLE_MODELS;
    }

    @Override
    public String updateCurrentModel(String modelName)
    {
        if (StringUtils.isEmpty(modelName))
        {
            throw new ServiceException("请选择 AI 模型");
        }
        boolean exists = AVAILABLE_MODELS.stream().anyMatch(item -> item.getModelName().equals(modelName));
        if (!exists)
        {
            throw new ServiceException("当前模型不可用，请重新选择");
        }

        Long userId = SecurityUtils.getUserId();
        if (userId == null)
        {
            aiProperties.setModel(modelName);
            return modelName;
        }

        String configKey = buildUserModelKey(userId);
        SysConfig current = sysConfigMapper.checkConfigKeyUnique(configKey);
        if (current == null)
        {
            SysConfig config = new SysConfig();
            config.setConfigName("AI模型偏好-" + userId);
            config.setConfigKey(configKey);
            config.setConfigValue(modelName);
            config.setConfigType("N");
            config.setCreateBy(SecurityUtils.getUsername());
            config.setRemark("记录当前账号默认 AI 模型");
            sysConfigMapper.insertConfig(config);
        }
        else
        {
            current.setConfigValue(modelName);
            current.setUpdateBy(SecurityUtils.getUsername());
            current.setRemark("记录当前账号默认 AI 模型");
            sysConfigMapper.updateConfig(current);
        }
        return modelName;
    }

    private String invoke(String businessType, Long bizId, String prompt)
    {
        String currentModel = resolveCurrentModel();
        EduAiLog log = new EduAiLog();
        log.setBusinessType(businessType);
        log.setBizId(bizId);
        log.setUserId(SecurityUtils.getUserId());
        log.setUserName(SecurityUtils.getUsername());
        log.setRoleType(resolveRoleType());
        log.setPromptContent(prompt);
        log.setModelName(currentModel);
        long start = System.currentTimeMillis();

        try
        {
            validatePrompt(prompt, log);
            String content;
            if (!aiProperties.isEnabled() || StringUtils.isEmpty(aiProperties.getEndpoint()) || StringUtils.isEmpty(aiProperties.getApiKey()))
            {
                content = buildMockResponse(businessType);
                log.setStatus("mock");
            }
            else
            {
                content = requestRemote(currentModel, prompt);
                log.setStatus("success");
            }
            log.setResponseContent(content);
            log.setRiskFlag("normal");
            log.setLatencyMs(System.currentTimeMillis() - start);
            log.setPromptTokens(estimateTokens(prompt));
            log.setCompletionTokens(estimateTokens(content));
            aiLogService.insertAiLog(log);
            return content;
        }
        catch (Exception e)
        {
            log.setStatus("failed");
            log.setRiskFlag("review");
            log.setErrorMessage(e.getMessage());
            log.setLatencyMs(System.currentTimeMillis() - start);
            aiLogService.insertAiLog(log);
            throw new ServiceException("AI 调用失败: " + e.getMessage());
        }
    }

    private void validatePrompt(String prompt, EduAiLog log)
    {
        if (StringUtils.isEmpty(prompt))
        {
            throw new ServiceException("AI 输入内容不能为空");
        }
        if (prompt.length() > aiProperties.getMaxPromptLength())
        {
            throw new ServiceException("输入内容过长，请精简后重试");
        }
        List<String> bannedKeywords = aiProperties.getBannedKeywords();
        if (bannedKeywords != null)
        {
            for (String keyword : bannedKeywords)
            {
                if (StringUtils.isNotEmpty(keyword) && prompt.contains(keyword))
                {
                    log.setStatus("blocked");
                    log.setRiskFlag("blocked");
                    log.setErrorMessage("命中敏感词: " + keyword);
                    aiLogService.insertAiLog(log);
                    throw new ServiceException("输入内容包含受限关键词，请调整后重试");
                }
            }
        }
    }

    private String requestRemote(String modelName, String prompt) throws Exception
    {
        JSONObject body = new JSONObject();
        body.put("model", modelName);
        body.put("messages", JSONArray.of(
                JSONObject.of("role", "system", "content", "你是中小学课后服务平台的 AI 助手，请直接输出安全、简洁、可展示的中文内容。除非用户要求，否则不要输出 Markdown 标记。"),
                JSONObject.of("role", "user", "content", prompt)));
        body.put("temperature", 0.4);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(aiProperties.getEndpoint()))
                .timeout(Duration.ofSeconds(aiProperties.getTimeoutSeconds()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + aiProperties.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString()))
                .build();

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(aiProperties.getTimeoutSeconds()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 403)
        {
            throw new ServiceException("远程接口返回状态码 403");
        }
        if (response.statusCode() >= 300)
        {
            throw new ServiceException("远程接口返回状态码 " + response.statusCode());
        }

        JSONObject json = JSON.parseObject(response.body());
        JSONArray choices = json.getJSONArray("choices");
        if (choices == null || choices.isEmpty())
        {
            throw new ServiceException("AI 未返回有效结果");
        }
        return choices.getJSONObject(0).getJSONObject("message").getString("content");
    }

    private String buildHomeworkPrompt(String prompt)
    {
        return "请根据学生问题给出清晰、友好的课后答疑，控制在1到2段，不要使用Markdown。\n" + prompt;
    }

    private String buildNoticePrompt(String prompt)
    {
        return "请生成一段适合课后服务平台发布的课程通知，控制在120字以内，语气正式清晰，不要使用Markdown。\n" + prompt;
    }

    private String buildTeachingPrompt(String prompt)
    {
        return "请生成3条简洁可执行的教学建议，适合教师直接查看与使用，不要使用Markdown。\n" + prompt;
    }

    private String buildRecommendationPrompt(String prompt)
    {
        return "请根据学生档案和课程信息，只输出一句简洁的推荐理由，控制在30字以内，不要使用Markdown，不要分点。\n" + prompt;
    }

    private String buildOnlineResourcePrompt(String prompt)
    {
        return "你是中小学课后网课资源推荐助手。请严格根据给定资源库进行推荐，不要杜撰资源，不要输出Markdown，不要解释过程。"
                + "请只返回JSON数组，最多3项，格式为"
                + "[{\"title\":\"资源标题\",\"reason\":\"20字内推荐理由\",\"link\":\"资源链接\"}]。"
                + "若没有合适资源，返回空数组[]。\n" + prompt;
    }

    private String buildMockResponse(String businessType)
    {
        if ("homework_answer".equals(businessType))
        {
            return "先别着急重做，先检查关键步骤。\n把不会的部分拆开看，再按顺序重新尝试一次。";
        }
        if ("course_notice".equals(businessType))
        {
            return "请同学们提前10分钟到教室，带好学习用品，按时参加课后课程。";
        }
        if ("course_recommendation".equals(businessType))
        {
            return "课程内容与学生兴趣较匹配，适合作为优先报名选择。";
        }
        if ("online_resource_recommendation".equals(businessType))
        {
            return "[{\"title\":\"国家中小学智慧教育平台\",\"reason\":\"适合系统补充课堂知识\",\"link\":\"https://basic.smartedu.cn/\"}]";
        }
        return "已生成可直接使用的教学辅助内容。\n建议结合实际情况做进一步微调。";
    }

    private String resolveRoleType()
    {
        if (SecurityUtils.hasRole("edu_teacher"))
        {
            return "teacher";
        }
        if (SecurityUtils.hasRole("edu_parent"))
        {
            return "parent";
        }
        if (SecurityUtils.hasRole("edu_student"))
        {
            return "student";
        }
        return "admin";
    }

    private String resolveCurrentModel()
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null)
        {
            return aiProperties.getModel();
        }
        SysConfig config = sysConfigMapper.checkConfigKeyUnique(buildUserModelKey(userId));
        if (config != null && StringUtils.isNotEmpty(config.getConfigValue()))
        {
            return config.getConfigValue();
        }
        return aiProperties.getModel();
    }

    private String buildUserModelKey(Long userId)
    {
        return USER_MODEL_KEY_PREFIX + userId;
    }

    private Integer estimateTokens(String content)
    {
        return StringUtils.isEmpty(content) ? 0 : Math.max(1, content.length() / 4);
    }
}
