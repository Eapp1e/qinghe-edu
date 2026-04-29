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
            new EduAiModelOption("deepseek-ai/DeepSeek-V3.2", "DeepSeek V3.2", "综合能力最均衡，适合作业问答、家长陪学建议和课程推荐，已完成真实联调"),
            new EduAiModelOption("Qwen/Qwen2.5-72B-Instruct", "Qwen 2.5 72B", "中文指令理解稳定，适合正式通知、长文本建议和教学内容生成，已完成真实联调"),
            new EduAiModelOption("THUDM/GLM-4-32B-0414", "GLM-4 32B", "表达风格稳健，适合作为多模型对照和通用文本生成备选，已完成真实联调"));

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
    public String generateParentDiagnosis(Long studentUserId, String prompt)
    {
        return invoke("parent_diagnosis", studentUserId, buildParentDiagnosisPrompt(prompt));
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
            validateRuntimeConfig();
            String content = requestRemote(currentModel, prompt);
            log.setStatus("success");
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

    private void validateRuntimeConfig()
    {
        if (!aiProperties.isEnabled())
        {
            throw new ServiceException("AI 服务未启用，请先完成模型服务配置");
        }
        if (StringUtils.isEmpty(aiProperties.getEndpoint()))
        {
            throw new ServiceException("AI 服务地址未配置，请先配置模型接口地址");
        }
        if (StringUtils.isEmpty(aiProperties.getApiKey()))
        {
            throw new ServiceException("AI API Key 未配置，请先配置真实模型密钥");
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
                JSONObject.of("role", "system", "content", "你是中小学课后服务平台的 AI 助手，请直接输出安全、简洁、可展示的中文内容。除非用户要求，否则不要输出 Markdown 表格。"),
                JSONObject.of("role", "user", "content", prompt)));
        body.put("temperature", 0.4);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(aiProperties.getEndpoint()))
                .timeout(Duration.ofSeconds(resolveTimeoutSeconds()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + aiProperties.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString()))
                .build();

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(resolveTimeoutSeconds()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 403)
        {
            throw new ServiceException("远程接口返回状态码 403，请检查模型密钥或账号权限");
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
        JSONObject message = choices.getJSONObject(0).getJSONObject("message");
        String content = message == null ? null : message.getString("content");
        if (StringUtils.isEmpty(StringUtils.trim(content)))
        {
            throw new ServiceException("AI 未返回可展示内容");
        }
        return content.trim();
    }

    private Integer resolveTimeoutSeconds()
    {
        Integer timeoutSeconds = aiProperties.getTimeoutSeconds();
        return timeoutSeconds == null || timeoutSeconds <= 0 ? 30 : timeoutSeconds;
    }

    private String buildHomeworkPrompt(String prompt)
    {
        return "请根据学生问题给出清晰、友好的课后答疑，控制在1到3段，不要使用Markdown。\n" + prompt;
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
        return "请根据学生档案和候选课程，给每门候选课程生成具体推荐理由。"
                + "只返回JSON数组，数组项格式为：{\"courseName\":\"课程名称\",\"reason\":\"30字以内推荐理由\"}。"
                + "推荐理由必须结合孩子年级、兴趣或课程内容，不要使用Markdown，不要输出无关说明。\n" + prompt;
    }

    private String buildOnlineResourcePrompt(String prompt)
    {
        return "你是中小学课后网课资源推荐助手。请严格根据给定资源库进行推荐，不要杜撰资源，不要输出Markdown，不要解释过程。"
                + "请只返回JSON数组，最多4项，格式为："
                + "[{\"title\":\"资源标题\",\"reason\":\"20字内推荐理由\",\"link\":\"资源链接\"}]。"
                + "若没有合适资源，返回空数组[]。\n" + prompt;
    }

    private String buildParentDiagnosisPrompt(String prompt)
    {
        return "你是青禾智学课后服务平台的家庭教育顾问。请结合学生学习记录、作业问答和家长反馈，给家长输出一份温和、具体、可执行的中文建议。"
                + "结构请包含：1. 孩子当前状态；2. 家长可以怎么做；3. 本周家庭陪伴小约定。"
                + "避免责备孩子或家长，不要使用夸张诊断，不要输出 Markdown 表格。\n" + prompt;
    }

    private String resolveRoleType()
    {
        try
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
        }
        catch (Exception ignored)
        {
            // AI logging must not fail just because the security context is incomplete in tests or background jobs.
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
