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
            new EduAiModelOption("Qwen/Qwen2.5-7B-Instruct", "Qwen 2.5 7B", "已验证可用，响应更快，适合课程通知与日常教学建议"),
            new EduAiModelOption("deepseek-ai/DeepSeek-V3", "DeepSeek V3", "已验证可用，生成质量更高，适合正式内容与更完整建议"));

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
            throw new ServiceException("??? AI ??");
        }
        boolean exists = AVAILABLE_MODELS.stream().anyMatch(item -> item.getModelName().equals(modelName));
        if (!exists)
        {
            throw new ServiceException("???????????");
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
            config.setConfigName("AI????-" + userId);
            config.setConfigKey(configKey);
            config.setConfigValue(modelName);
            config.setConfigType("N");
            config.setCreateBy(SecurityUtils.getUsername());
            config.setRemark("????? AI ?????????");
            sysConfigMapper.insertConfig(config);
        }
        else
        {
            current.setConfigValue(modelName);
            current.setUpdateBy(SecurityUtils.getUsername());
            current.setRemark("????? AI ?????????");
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
            throw new ServiceException("AI ????: " + e.getMessage());
        }
    }

    private void validatePrompt(String prompt, EduAiLog log)
    {
        if (StringUtils.isEmpty(prompt))
        {
            throw new ServiceException("AI ??????");
        }
        if (prompt.length() > aiProperties.getMaxPromptLength())
        {
            throw new ServiceException("?????????????");
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
                    log.setErrorMessage("?????: " + keyword);
                    aiLogService.insertAiLog(log);
                    throw new ServiceException("?????????????????");
                }
            }
        }
    }

    private String requestRemote(String modelName, String prompt) throws Exception
    {
        JSONObject body = new JSONObject();
        body.put("model", modelName);
        body.put("messages", JSONArray.of(
                JSONObject.of("role", "system", "content", "???????????? AI ??????????????????????????? 3 ?????????? Markdown ???? **?#?-?``` ??"),
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
            throw new ServiceException("??????????????? AI ????????????");
        }
        if (response.statusCode() >= 300)
        {
            throw new ServiceException("????????? " + response.statusCode());
        }

        JSONObject json = JSON.parseObject(response.body());
        JSONArray choices = json.getJSONArray("choices");
        if (choices == null || choices.isEmpty())
        {
            throw new ServiceException("???????????");
        }
        return choices.getJSONObject(0).getJSONObject("message").getString("content");
    }

    private String buildHomeworkPrompt(String prompt)
    {
        return "??????????????????????????????? 1 ? 2 ?????? Markdown ???\n" + prompt;
    }

    private String buildNoticePrompt(String prompt)
    {
        return "????????????????????? 120 ???????????????? 1 ???????? Markdown ???\n" + prompt;
    }

    private String buildTeachingPrompt(String prompt)
    {
        return "??????????????????? 3 ????????????????????????????????? Markdown ???\n" + prompt;
    }

    private String buildRecommendationPrompt(String prompt)
    {
        return "请根据学生档案和课程信息，只输出一句简洁的推荐理由，控制在30字以内，不要使用Markdown，不要分点。\n" + prompt;
    }

    private String buildMockResponse(String businessType)
    {
        if ("homework_answer".equals(businessType))
        {
            return "?????????????????\n??????????????????????\n???????????????????";
        }
        if ("course_notice".equals(businessType))
        {
            return "????????????????? 10 ?????????????";
        }
        if ("course_recommendation".equals(businessType))
        {
            return "课程内容与学生兴趣较匹配，适合作为优先报名选择。";
        }
        return "?????????????????????\n????????????????????\n???????????????????";
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
