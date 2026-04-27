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
            new EduAiModelOption("Qwen/Qwen2.5-7B-Instruct", "Qwen 2.5 7B", "已验证可用，响应更快，适合日常教学与学习辅导"),
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
            String content;
            if (!aiProperties.isEnabled() || StringUtils.isEmpty(aiProperties.getEndpoint()) || StringUtils.isEmpty(aiProperties.getApiKey()))
            {
                content = buildMockResponse(businessType, prompt);
                log.setStatus("success");
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
                JSONObject.of("role", "system", "content", "你是中小学课后服务平台的 AI 助手，请直接输出安全、简洁、可展示的中文内容。除非用户要求，否则不要输出 Markdown 表格。"),
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

    private String buildParentDiagnosisPrompt(String prompt)
    {
        return "你是青禾智学课后服务平台的家庭教育顾问。请结合学生学习记录、作业问答和家长反馈，给家长输出一份温和、具体、可执行的中文建议。"
                + "结构请包含：1. 孩子当前状态；2. 家长可以怎么做；3. 本周家庭陪伴小约定。"
                + "避免责备孩子或家长，不要使用夸张诊断，不要输出 Markdown 表格。\n" + prompt;
    }

    private String buildMockResponse(String businessType, String prompt)
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
        if ("parent_diagnosis".equals(businessType))
        {
            return buildParentDiagnosisContent(prompt);
        }
        return "已生成可直接使用的教学辅助内容。\n建议结合实际情况做进一步微调。";
    }

    private String buildParentDiagnosisContent(String prompt)
    {
        String student = extractBetween(prompt, "学生：", "\n");
        String concern = extractAfter(prompt, "家长补充关注：");
        if (StringUtils.isEmpty(student))
        {
            student = "孩子";
        }
        if (StringUtils.isEmpty(concern))
        {
            concern = "近期学习与生活习惯需要持续观察";
        }

        String state = student + "近期的主要信号集中在“" + concern + "”。结合学习记录、教师反馈和作业问答来看，问题更像是任务启动、情绪承受或习惯稳定性需要支持，而不是孩子单纯不配合。";
        String action = "家长可以先把目标缩小到每天一个可完成动作：先认可孩子已经做到的部分，再一起约定开始时间、完成标准和复盘方式。沟通时尽量少用评价句，多用“你打算先做哪一步”“需要我陪你几分钟”这类选择型问题。";
        String agreement = "本周家庭陪伴小约定：连续5天完成一个15到30分钟的小任务，孩子完成后用照片或一句话反馈，家长当天给出具体肯定，并奖励5到10分家庭积分。";

        if (concern.contains("拖") || concern.contains("作业") || concern.contains("磨蹭") || concern.contains("启动"))
        {
            state = student + "在作业启动和时间感上需要支持，容易把困难任务拖到最后，但只要步骤被拆小，完成度通常会提升。";
            action = "建议家长使用“先做10分钟”策略：先陪孩子列出3个小步骤，只提醒开始和收尾，不在过程中频繁催促；完成一个步骤就即时确认。";
            agreement = "本周约定：每天固定一个作业启动时间，先完成最容易的一项，连续5天达成后兑换一次周末自主活动奖励。";
        }
        else if (concern.contains("阅读") || concern.contains("读书"))
        {
            state = student + "的阅读坚持度还不稳定，可能需要从兴趣、陪伴氛围和可见反馈三个方面同时建立正循环。";
            action = "建议家长先让孩子自己选书，每天共读或安静陪读20分钟，结束后只问一个开放问题，比如“今天哪个情节最有画面感”。";
            agreement = "本周约定：完成3次20分钟阅读打卡，每次记录一句喜欢的内容，累计后兑换一次家庭电影或亲子外出。";
        }
        else if (concern.contains("沟通") || concern.contains("顶嘴") || concern.contains("冲突") || concern.contains("情绪"))
        {
            state = student + "近期可能更需要被听见，冲突表面是态度问题，背后常常是自主感和情绪出口不足。";
            action = "建议家长先暂停说教，使用“三句法”：我看到你很着急；我想先听你说完；我们一起选一个解决办法。等情绪降下来再谈规则。";
            agreement = "本周约定：每天睡前8分钟亲子聊天，只聊一件开心事和一件困难事，家长不评价，只复述和回应。";
        }
        else if (concern.contains("整理") || concern.contains("忘带") || concern.contains("习惯"))
        {
            state = student + "在物品整理和习惯保持上还依赖外部提醒，需要把抽象要求变成可看见、可检查的小清单。";
            action = "建议家长和孩子一起做一张“出门前三件事”清单，贴在书包旁；前3天陪同检查，之后逐步改为孩子自查。";
            agreement = "本周约定：连续4天睡前整理书包和桌面，拍照打卡，累计后兑换一次自选晚餐或亲子游戏时间。";
        }
        else if (concern.contains("运动") || concern.contains("体能"))
        {
            state = student + "需要在学习之外补充身体活动，运动能帮助释放压力，也能改善注意力和睡眠节律。";
            action = "建议家长不要把运动做成考核，先从饭后散步、跳绳或亲子球类15分钟开始，重点看坚持而不是成绩。";
            agreement = "本周约定：完成3次亲子运动打卡，每次不少于15分钟，达成后兑换一次户外活动安排。";
        }
        return "孩子当前状态：" + state + "\n家长可以怎么做：" + action + "\n本周家庭陪伴小约定：" + agreement;
    }

    private String buildParentDiagnosisMock(String prompt)
    {
        String student = extractBetween(prompt, "学生：", "\n");
        String concern = extractAfter(prompt, "家长补充关注：");
        if (StringUtils.isEmpty(student))
        {
            student = "孩子";
        }
        if (StringUtils.isEmpty(concern))
        {
            concern = "近期学习与生活习惯需要持续观察";
        }

        String state = student + "近期的主要信号集中在“" + concern + "”。从学习记录和问答表现看，孩子并不是完全不愿意配合，更像是在任务启动、情绪表达或持续坚持上需要一个更清晰的家庭支持结构。";
        String action = "家长可以先把目标缩小到每天一个可完成动作：先认可孩子已经做到的部分，再一起约定开始时间、完成标准和复盘方式。沟通时尽量少用评价句，多用“你打算先做哪一步”“需要我陪你几分钟”这类选择型问题。";
        String agreement = "本周家庭小约定：连续5天完成一个15-30分钟的小任务，孩子完成后用照片或一句话反馈，家长当天给出具体肯定，并奖励5-10分家庭积分。";

        if (concern.contains("拖") || concern.contains("作业") || concern.contains("磨蹭"))
        {
            state = student + "在作业启动和时间感上需要支持，容易把困难任务拖到最后，但只要步骤被拆小，完成度通常会提升。";
            action = "建议家长使用“先做10分钟”策略：先陪孩子列出3个小步骤，只提醒开始和收尾，不在过程中频繁催促；完成一个步骤就即时确认。";
            agreement = "本周约定：每天固定一个作业启动时间，先完成最容易的一项，连续5天达成可获得一次周末自主活动奖励。";
        }
        else if (concern.contains("阅读") || concern.contains("读书"))
        {
            state = student + "的阅读坚持度还不稳定，可能需要从兴趣、陪伴氛围和可见反馈三个方面同时建立正循环。";
            action = "建议家长先让孩子自己选书，每天共读或安静陪读20分钟，结束后只问一个开放问题，比如“今天哪个情节最有画面感”。";
            agreement = "本周约定：完成5次20分钟阅读打卡，每次记录一句喜欢的内容，累计后兑换一次家庭电影或亲子外出。";
        }
        else if (concern.contains("沟通") || concern.contains("顶嘴") || concern.contains("冲突"))
        {
            state = student + "近期可能更需要被听见，冲突表面是态度问题，背后常常是自主感和情绪出口不足。";
            action = "建议家长先暂停说教，使用“三句法”：我看到你很着急；我想先听你说完；我们一起选一个解决办法。等情绪降下来再谈规则。";
            agreement = "本周约定：每天睡前8分钟亲子聊天，只聊一件开心事和一件困难事，家长不评价，只复述和回应。";
        }
        else if (concern.contains("整理") || concern.contains("忘带") || concern.contains("习惯"))
        {
            state = student + "在物品整理和习惯保持上还依赖外部提醒，需要把抽象要求变成可看见、可检查的小清单。";
            action = "建议家长和孩子一起做一张“出门前三件事”清单，贴在书包旁；前3天陪同检查，之后逐步改为孩子自查。";
            agreement = "本周约定：连续5天睡前整理书包和桌面，拍照打卡，累计后兑换一次自选晚餐或亲子游戏时间。";
        }
        else if (concern.contains("运动") || concern.contains("体能"))
        {
            state = student + "需要在学习之外补充身体活动，运动能帮助释放压力，也能改善注意力和睡眠节律。";
            action = "建议家长不要把运动做成考核，先从饭后散步、跳绳或亲子球类15分钟开始，重点看坚持而不是成绩。";
            agreement = "本周约定：完成4次亲子运动打卡，每次不少于15分钟，达成后兑换一次户外活动安排。";
        }
        return "孩子当前状态：" + state + "\n家长可以怎么做：" + action + "\n本周家庭陪伴小约定：" + agreement;
    }

    private String extractBetween(String content, String start, String end)
    {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(start))
        {
            return "";
        }
        int startIndex = content.indexOf(start);
        if (startIndex < 0)
        {
            return "";
        }
        int valueStart = startIndex + start.length();
        int endIndex = StringUtils.isEmpty(end) ? -1 : content.indexOf(end, valueStart);
        return (endIndex > valueStart ? content.substring(valueStart, endIndex) : content.substring(valueStart)).trim();
    }

    private String extractAfter(String content, String start)
    {
        String value = extractBetween(content, start, "\n");
        return value.length() > 120 ? value.substring(0, 120) : value;
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
