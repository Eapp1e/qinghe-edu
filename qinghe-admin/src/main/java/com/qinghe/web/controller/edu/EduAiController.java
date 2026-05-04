package com.qinghe.web.controller.edu;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.qinghe.common.annotation.Log;
import com.qinghe.common.core.controller.BaseController;
import com.qinghe.common.core.domain.AjaxResult;
import com.qinghe.common.enums.BusinessType;
import com.qinghe.common.exception.ServiceException;
import com.qinghe.common.utils.SecurityUtils;
import com.qinghe.common.utils.StringUtils;
import com.qinghe.system.domain.edu.EduCourseEnrollment;
import com.qinghe.system.domain.edu.EduHomeworkQuestion;
import com.qinghe.system.domain.edu.EduStudentProfile;
import com.qinghe.system.service.edu.IEduAiService;
import com.qinghe.system.service.edu.IEduEnrollmentService;
import com.qinghe.system.service.edu.IEduHomeworkQuestionService;
import com.qinghe.system.service.edu.IEduStudentProfileService;

@RestController
@RequestMapping("/edu/ai")
public class EduAiController extends BaseController
{
    @Autowired
    private IEduAiService aiService;

    @Autowired
    private IEduStudentProfileService profileService;

    @Autowired
    private IEduEnrollmentService enrollmentService;

    @Autowired
    private IEduHomeworkQuestionService questionService;

    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('edu_admin') or @ss.hasRole('edu_teacher') or @ss.hasRole('edu_parent') or @ss.hasRole('edu_student')")
    @GetMapping("/models")
    public AjaxResult models()
    {
        return success(aiService.getAvailableModels());
    }

    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('edu_admin') or @ss.hasRole('edu_teacher') or @ss.hasRole('edu_parent') or @ss.hasRole('edu_student')")
    @GetMapping("/currentModel")
    public AjaxResult currentModel()
    {
        return success(aiService.getCurrentModel());
    }

    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('edu_admin') or @ss.hasRole('edu_teacher') or @ss.hasRole('edu_parent') or @ss.hasRole('edu_student')")
    @PutMapping("/currentModel")
    public AjaxResult updateCurrentModel(@RequestBody ModelBody body)
    {
        return success(aiService.updateCurrentModel(body.getModelName()));
    }

    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('edu_admin') or @ss.hasRole('edu_teacher') or @ss.hasRole('edu_parent') or @ss.hasRole('edu_student')")
    @Log(title = "网课推荐", businessType = BusinessType.OTHER)
    @PostMapping("/online-resource-recommend")
    public AjaxResult onlineResourceRecommend(@RequestBody OnlineResourceBody body)
    {
        if (body == null || StringUtils.isEmpty(body.getInterest()))
        {
            throw new ServiceException("请输入想学习的内容");
        }
        if (body.getResources() == null || body.getResources().isEmpty())
        {
            throw new ServiceException("当前资源库为空，暂时无法推荐");
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("用户想学内容：").append(body.getInterest()).append("\n");
        prompt.append("资源库：\n");
        for (OnlineResourceItem item : body.getResources())
        {
            if (item == null || StringUtils.isEmpty(item.getTitle()) || StringUtils.isEmpty(item.getLink()))
            {
                continue;
            }
            prompt.append("- 标题：").append(item.getTitle())
                    .append("；来源：").append(StringUtils.defaultString(item.getSource(), ""))
                    .append("；分类：").append(StringUtils.defaultString(item.getCategory(), ""))
                    .append("；描述：").append(StringUtils.defaultString(item.getDescription(), ""))
                    .append("；链接：").append(item.getLink())
                    .append("\n");
        }

        String content = aiService.generateOnlineResourceRecommendation(SecurityUtils.getUserId(), prompt.toString());
        return success(normalizeRecommendationList(content));
    }

    @PreAuthorize("@ss.hasRole('edu_parent')")
    @Log(title = "家长诊断建议", businessType = BusinessType.OTHER)
    @PostMapping("/parent-diagnosis")
    public AjaxResult parentDiagnosis(@RequestBody ParentDiagnosisBody body)
    {
        if (body == null || body.getStudentUserId() == null)
        {
            throw new ServiceException("请选择要诊断的孩子");
        }
        if (StringUtils.isEmpty(StringUtils.trim(body.getConcern())))
        {
            throw new ServiceException("请填写近期关注，AI 建议会围绕该问题生成");
        }
        body.setConcern(StringUtils.trim(body.getConcern()));
        EduStudentProfile child = findCurrentParentChild(body.getStudentUserId());
        String prompt = buildParentDiagnosisPrompt(child, body);
        return AjaxResult.success("生成成功", aiService.generateParentDiagnosis(child.getStudentUserId(), prompt));
    }

    private String buildParentDiagnosisPrompt(EduStudentProfile child, ParentDiagnosisBody body)
    {
        EduCourseEnrollment enrollmentQuery = new EduCourseEnrollment();
        enrollmentQuery.setStudentUserId(child.getStudentUserId());
        List<EduCourseEnrollment> enrollments = enrollmentService.selectEnrollmentList(enrollmentQuery);

        EduHomeworkQuestion questionQuery = new EduHomeworkQuestion();
        questionQuery.setStudentUserId(child.getStudentUserId());
        List<EduHomeworkQuestion> questions = questionService.selectQuestionList(questionQuery);

        StringBuilder prompt = new StringBuilder();
        prompt.append("学生：").append(child.getStudentName()).append("\n");
        prompt.append("年级班级：").append(StringUtils.defaultString(child.getGradeName(), "未填写"))
                .append(StringUtils.defaultString(child.getClassName(), "")).append("\n");
        prompt.append("兴趣标签：").append(truncateText(StringUtils.defaultString(child.getInterestTags(), "未填写"), 50)).append("\n");
        prompt.append("家长补充关注：").append(truncateText(body.getConcern(), 80)).append("\n");
        prompt.append("近期学习记录摘要：\n");
        appendEnrollmentSummary(prompt, enrollments);
        prompt.append("近期作业问答摘要：\n");
        appendQuestionSummary(prompt, questions);
        return prompt.toString();
    }

    private void appendEnrollmentSummary(StringBuilder prompt, List<EduCourseEnrollment> enrollments)
    {
        int count = 0;
        for (EduCourseEnrollment item : enrollments)
        {
            if (item == null || count >= 3)
            {
                break;
            }
            prompt.append("- ").append(truncateText(item.getCourseName(), 28))
                    .append("：学习记录=").append(truncateText(StringUtils.defaultString(item.getLearningRecord(), "暂无记录"), 45))
                    .append("；教师反馈=").append(truncateText(StringUtils.defaultString(item.getInteractionSummary(), "暂无"), 45))
                    .append("\n");
            count++;
        }
        if (count == 0)
        {
            prompt.append("- 暂无课程学习记录\n");
        }
    }

    private void appendQuestionSummary(StringBuilder prompt, List<EduHomeworkQuestion> questions)
    {
        int count = 0;
        for (EduHomeworkQuestion item : questions)
        {
            if (item == null || count >= 3)
            {
                break;
            }
            prompt.append("- ").append(truncateText(item.getQuestionTitle(), 30))
                    .append("：问题=").append(truncateText(StringUtils.defaultString(item.getQuestionContent(), ""), 45))
                    .append("；AI答复=").append(truncateText(StringUtils.defaultString(item.getAiAnswer(), "暂无"), 45))
                    .append("\n");
            count++;
        }
        if (count == 0)
        {
            prompt.append("- 暂无作业问答记录\n");
        }
    }

    private String truncateText(String text, int maxLength)
    {
        String value = StringUtils.defaultString(text, "").trim();
        if (value.length() <= maxLength)
        {
            return value;
        }
        return value.substring(0, maxLength) + "…";
    }

    private EduStudentProfile findCurrentParentChild(Long studentUserId)
    {
        List<EduStudentProfile> children = profileService.selectCurrentUserChildren();
        for (EduStudentProfile child : children)
        {
            if (child != null && studentUserId.equals(child.getStudentUserId()))
            {
                return child;
            }
        }
        throw new ServiceException("只能查看已绑定孩子的诊断建议");
    }

    private List<JSONObject> normalizeRecommendationList(String content)
    {
        JSONArray array = parseRecommendationArray(content);
        List<JSONObject> result = new ArrayList<>();
        for (int i = 0; i < array.size(); i++)
        {
            Object item = array.get(i);
            if (item instanceof JSONObject)
            {
                JSONObject object = (JSONObject) item;
                if (StringUtils.isNotEmpty(object.getString("title")) && StringUtils.isNotEmpty(object.getString("link")))
                {
                    result.add(object);
                }
            }
        }
        return result;
    }

    private JSONArray parseRecommendationArray(String content)
    {
        if (StringUtils.isEmpty(content))
        {
            return new JSONArray();
        }

        JSONArray parsed = tryParseArray(content);
        if (parsed != null)
        {
            return parsed;
        }

        String normalized = content.trim()
                .replace("```json", "")
                .replace("```JSON", "")
                .replace("```", "")
                .trim();

        parsed = tryParseArray(normalized);
        if (parsed != null)
        {
            return parsed;
        }

        try
        {
            JSONObject object = JSON.parseObject(normalized);
            JSONArray array = object.getJSONArray("recommendations");
            if (array == null)
            {
                array = object.getJSONArray("data");
            }
            if (array == null)
            {
                array = object.getJSONArray("items");
            }
            if (array != null)
            {
                return array;
            }
        }
        catch (Exception ignored)
        {
        }

        int start = normalized.indexOf('[');
        int end = normalized.lastIndexOf(']');
        if (start >= 0 && end > start)
        {
            parsed = tryParseArray(normalized.substring(start, end + 1));
            if (parsed != null)
            {
                return parsed;
            }
        }
        return parseLineRecommendations(normalized);
    }

    private JSONArray tryParseArray(String text)
    {
        try
        {
            return JSON.parseArray(text);
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    private JSONArray parseLineRecommendations(String text)
    {
        JSONArray result = new JSONArray();
        if (StringUtils.isEmpty(text))
        {
            return result;
        }
        Pattern linkPattern = Pattern.compile("(https?://[^\\s\\]，；]+)");
        String[] lines = text.split("\\r?\\n");
        for (String rawLine : lines)
        {
            String line = StringUtils.trim(rawLine);
            if (StringUtils.isEmpty(line))
            {
                continue;
            }
            Matcher matcher = linkPattern.matcher(line);
            if (!matcher.find())
            {
                continue;
            }
            String link = matcher.group(1);
            String title = line.substring(0, matcher.start()).replaceAll("^[\\-\\d\\.、\\s]+", "").trim();
            String reason = line.substring(matcher.end()).replaceAll("^[，；:：\\-\\s]+", "").trim();
            JSONObject object = new JSONObject();
            object.put("title", StringUtils.isEmpty(title) ? "推荐资源" : title);
            object.put("link", link);
            object.put("reason", StringUtils.isEmpty(reason) ? "与当前学习兴趣较匹配" : reason);
            result.add(object);
        }
        return result;
    }

    public static class ModelBody
    {
        private String modelName;

        public String getModelName()
        {
            return modelName;
        }

        public void setModelName(String modelName)
        {
            this.modelName = modelName;
        }
    }

    public static class OnlineResourceBody
    {
        private String interest;

        private List<OnlineResourceItem> resources = new ArrayList<>();

        public String getInterest()
        {
            return interest;
        }

        public void setInterest(String interest)
        {
            this.interest = interest;
        }

        public List<OnlineResourceItem> getResources()
        {
            return resources;
        }

        public void setResources(List<OnlineResourceItem> resources)
        {
            this.resources = resources;
        }
    }

    public static class OnlineResourceItem
    {
        private String title;
        private String source;
        private String category;
        private String description;
        private String link;

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getSource()
        {
            return source;
        }

        public void setSource(String source)
        {
            this.source = source;
        }

        public String getCategory()
        {
            return category;
        }

        public void setCategory(String category)
        {
            this.category = category;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public String getLink()
        {
            return link;
        }

        public void setLink(String link)
        {
            this.link = link;
        }
    }

    public static class ParentDiagnosisBody
    {
        private Long studentUserId;

        private String concern;

        public Long getStudentUserId()
        {
            return studentUserId;
        }

        public void setStudentUserId(Long studentUserId)
        {
            this.studentUserId = studentUserId;
        }

        public String getConcern()
        {
            return concern;
        }

        public void setConcern(String concern)
        {
            this.concern = concern;
        }
    }
}
