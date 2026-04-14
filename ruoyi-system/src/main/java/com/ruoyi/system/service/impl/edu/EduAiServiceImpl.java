package com.ruoyi.system.service.impl.edu;

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
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.config.EduAiProperties;
import com.ruoyi.system.domain.edu.EduAiLog;
import com.ruoyi.system.service.edu.IEduAiLogService;
import com.ruoyi.system.service.edu.IEduAiService;

@Service
public class EduAiServiceImpl implements IEduAiService
{
    @Autowired
    private EduAiProperties aiProperties;

    @Autowired
    private IEduAiLogService aiLogService;

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

    private String invoke(String businessType, Long bizId, String prompt)
    {
        EduAiLog log = new EduAiLog();
        log.setBusinessType(businessType);
        log.setBizId(bizId);
        log.setUserId(SecurityUtils.getUserId());
        log.setUserName(SecurityUtils.getUsername());
        log.setRoleType(resolveRoleType());
        log.setPromptContent(prompt);
        log.setModelName(aiProperties.getModel());
        long start = System.currentTimeMillis();

        try
        {
            validatePrompt(prompt, log);
            String content;
            if (!aiProperties.isEnabled() || StringUtils.isEmpty(aiProperties.getEndpoint()) || StringUtils.isEmpty(aiProperties.getApiKey()))
            {
                content = buildMockResponse(businessType, prompt);
                log.setStatus("mock");
            }
            else
            {
                content = requestRemote(prompt);
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
            throw new ServiceException("AI 调用失败：" + e.getMessage());
        }
    }

    private void validatePrompt(String prompt, EduAiLog log)
    {
        if (StringUtils.isEmpty(prompt))
        {
            throw new ServiceException("AI 输入不能为空");
        }
        if (prompt.length() > aiProperties.getMaxPromptLength())
        {
            throw new ServiceException("输入内容过长，请精简后再试");
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
                    log.setErrorMessage("命中敏感词：" + keyword);
                    aiLogService.insertAiLog(log);
                    throw new ServiceException("输入内容触发安全限制，请修改后重试");
                }
            }
        }
    }

    private String requestRemote(String prompt) throws Exception
    {
        JSONObject body = new JSONObject();
        body.put("model", aiProperties.getModel());
        body.put("messages", JSONArray.of(
                JSONObject.of("role", "system", "content", "你是中小学课后服务平台的AI助手，回答必须安全、具体、适龄。"),
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
        if (response.statusCode() >= 300)
        {
            throw new ServiceException("远程接口返回状态码 " + response.statusCode());
        }

        JSONObject json = JSON.parseObject(response.body());
        JSONArray choices = json.getJSONArray("choices");
        if (choices == null || choices.isEmpty())
        {
            throw new ServiceException("远程接口未返回有效结果");
        }
        return choices.getJSONObject(0).getJSONObject("message").getString("content");
    }

    private String buildHomeworkPrompt(String prompt)
    {
        return "请以中小学课后辅导老师身份回答学生问题，分为“问题分析、解题思路、鼓励建议”三段，语言适龄。\n" + prompt;
    }

    private String buildNoticePrompt(String prompt)
    {
        return "请根据课程信息生成一份面向家长和学生的课后服务通知，包含课程目标、时间地点、注意事项。\n" + prompt;
    }

    private String buildTeachingPrompt(String prompt)
    {
        return "请根据课程信息生成教师教学建议，包含课堂组织、差异化辅导、家校沟通建议。\n" + prompt;
    }

    private String buildMockResponse(String businessType, String prompt)
    {
        if ("homework_answer".equals(businessType))
        {
            return "问题分析：这道题考查的是基础知识点，需要先找准题目中的已知条件。\n解题思路：建议先列出关键条件，再一步步推导，遇到公式题可先写公式再代入。\n鼓励建议：你已经提出了很清晰的问题，继续保持这种主动提问的习惯。";
        }
        if ("course_notice".equals(businessType))
        {
            return "【课后服务通知】本次课程将围绕核心知识巩固与兴趣拓展展开，请同学按时到课、携带学习用品，家长可关注课后学习记录反馈。";
        }
        return "1. 课堂组织：采用“讲解+互动+练习”结构提升参与度。\n2. 分层辅导：为基础薄弱学生准备提示卡，为进阶学生增加拓展任务。\n3. 家校沟通：课后同步课堂表现与改进建议。";
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

    private Integer estimateTokens(String content)
    {
        return StringUtils.isEmpty(content) ? 0 : Math.max(1, content.length() / 4);
    }
}
