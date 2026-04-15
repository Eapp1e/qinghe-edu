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
import com.eapple.system.domain.edu.EduAiLog;
import com.eapple.system.service.edu.IEduAiLogService;
import com.eapple.system.service.edu.IEduAiService;

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
            throw new ServiceException("AI 璋冪敤澶辫触锛? + e.getMessage());
        }
    }

    private void validatePrompt(String prompt, EduAiLog log)
    {
        if (StringUtils.isEmpty(prompt))
        {
            throw new ServiceException("AI 杈撳叆涓嶈兘涓虹┖");
        }
        if (prompt.length() > aiProperties.getMaxPromptLength())
        {
            throw new ServiceException("杈撳叆鍐呭杩囬暱锛岃绮剧畝鍚庡啀璇?);
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
                    log.setErrorMessage("鍛戒腑鏁忔劅璇嶏細" + keyword);
                    aiLogService.insertAiLog(log);
                    throw new ServiceException("杈撳叆鍐呭瑙﹀彂瀹夊叏闄愬埗锛岃淇敼鍚庨噸璇?);
                }
            }
        }
    }

    private String requestRemote(String prompt) throws Exception
    {
        JSONObject body = new JSONObject();
        body.put("model", aiProperties.getModel());
        body.put("messages", JSONArray.of(
                JSONObject.of("role", "system", "content", "浣犳槸涓皬瀛﹁鍚庢湇鍔″钩鍙扮殑AI鍔╂墜锛屽洖绛斿繀椤诲畨鍏ㄣ€佸叿浣撱€侀€傞緞銆?),
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
            throw new ServiceException("杩滅▼鎺ュ彛杩斿洖鐘舵€佺爜 " + response.statusCode());
        }

        JSONObject json = JSON.parseObject(response.body());
        JSONArray choices = json.getJSONArray("choices");
        if (choices == null || choices.isEmpty())
        {
            throw new ServiceException("杩滅▼鎺ュ彛鏈繑鍥炴湁鏁堢粨鏋?);
        }
        return choices.getJSONObject(0).getJSONObject("message").getString("content");
    }

    private String buildHomeworkPrompt(String prompt)
    {
        return "璇蜂互涓皬瀛﹁鍚庤緟瀵艰€佸笀韬唤鍥炵瓟瀛︾敓闂锛屽垎涓衡€滈棶棰樺垎鏋愩€佽В棰樻€濊矾銆侀紦鍔卞缓璁€濅笁娈碉紝璇█閫傞緞銆俓n" + prompt;
    }

    private String buildNoticePrompt(String prompt)
    {
        return "璇锋牴鎹绋嬩俊鎭敓鎴愪竴浠介潰鍚戝闀垮拰瀛︾敓鐨勮鍚庢湇鍔￠€氱煡锛屽寘鍚绋嬬洰鏍囥€佹椂闂村湴鐐广€佹敞鎰忎簨椤广€俓n" + prompt;
    }

    private String buildTeachingPrompt(String prompt)
    {
        return "璇锋牴鎹绋嬩俊鎭敓鎴愭暀甯堟暀瀛﹀缓璁紝鍖呭惈璇惧爞缁勭粐銆佸樊寮傚寲杈呭銆佸鏍℃矡閫氬缓璁€俓n" + prompt;
    }

    private String buildMockResponse(String businessType, String prompt)
    {
        if ("homework_answer".equals(businessType))
        {
            return "闂鍒嗘瀽锛氳繖閬撻鑰冩煡鐨勬槸鍩虹鐭ヨ瘑鐐癸紝闇€瑕佸厛鎵惧噯棰樼洰涓殑宸茬煡鏉′欢銆俓n瑙ｉ鎬濊矾锛氬缓璁厛鍒楀嚭鍏抽敭鏉′欢锛屽啀涓€姝ユ鎺ㄥ锛岄亣鍒板叕寮忛鍙厛鍐欏叕寮忓啀浠ｅ叆銆俓n榧撳姳寤鸿锛氫綘宸茬粡鎻愬嚭浜嗗緢娓呮櫚鐨勯棶棰橈紝缁х画淇濇寔杩欑涓诲姩鎻愰棶鐨勪範鎯€?;
        }
        if ("course_notice".equals(businessType))
        {
            return "銆愯鍚庢湇鍔￠€氱煡銆戞湰娆¤绋嬪皢鍥寸粫鏍稿績鐭ヨ瘑宸╁浐涓庡叴瓒ｆ嫇灞曞睍寮€锛岃鍚屽鎸夋椂鍒拌銆佹惡甯﹀涔犵敤鍝侊紝瀹堕暱鍙叧娉ㄨ鍚庡涔犺褰曞弽棣堛€?;
        }
        return "1. 璇惧爞缁勭粐锛氶噰鐢ㄢ€滆瑙?浜掑姩+缁冧範鈥濈粨鏋勬彁鍗囧弬涓庡害銆俓n2. 鍒嗗眰杈呭锛氫负鍩虹钖勫急瀛︾敓鍑嗗鎻愮ず鍗★紝涓鸿繘闃跺鐢熷鍔犳嫇灞曚换鍔°€俓n3. 瀹舵牎娌熼€氾細璇惧悗鍚屾璇惧爞琛ㄧ幇涓庢敼杩涘缓璁€?;
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
