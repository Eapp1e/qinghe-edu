package com.eapple.web.controller.edu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import com.eapple.common.annotation.Log;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.enums.BusinessType;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.domain.edu.EduCourseEnrollment;
import com.eapple.system.domain.edu.EduHomeworkQuestion;
import com.eapple.system.domain.edu.EduStudentProfile;
import com.eapple.system.service.edu.IEduAiService;
import com.eapple.system.service.edu.IEduEnrollmentService;
import com.eapple.system.service.edu.IEduHomeworkQuestionService;
import com.eapple.system.service.edu.IEduStudentProfileService;

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
        List<JSONObject> recommendations = normalizeRecommendationList(content);
        if (recommendations.isEmpty())
        {
            recommendations = buildLocalRecommendations(body.getInterest(), body.getResources());
        }
        return success(recommendations);
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
        String prompt = buildCleanParentDiagnosisPrompt(child, body);
        return success(aiService.generateParentDiagnosis(child.getStudentUserId(), prompt));
    }

    private String buildCleanParentDiagnosisPrompt(EduStudentProfile child, ParentDiagnosisBody body)
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
        prompt.append("兴趣标签：").append(StringUtils.defaultString(child.getInterestTags(), "未填写")).append("\n");
        prompt.append("家长补充关注：").append(StringUtils.defaultString(body.getConcern(), "未填写")).append("\n");
        prompt.append("学习记录：\n");
        for (EduCourseEnrollment item : enrollments)
        {
            prompt.append("- ").append(item.getCourseName())
                    .append("：").append(StringUtils.defaultString(item.getLearningRecord(), "暂无记录"))
                    .append("；教师反馈：").append(StringUtils.defaultString(item.getInteractionSummary(), "暂无"))
                    .append("\n");
        }
        prompt.append("作业问答情况：\n");
        for (EduHomeworkQuestion item : questions)
        {
            prompt.append("- ").append(item.getQuestionTitle())
                    .append("：").append(StringUtils.defaultString(item.getQuestionContent(), ""))
                    .append("；AI答复摘要：").append(StringUtils.defaultString(item.getAiAnswer(), "暂无"))
                    .append("\n");
        }
        return prompt.toString();
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

    private String buildParentDiagnosisPrompt(EduStudentProfile child, ParentDiagnosisBody body)
    {
        EduCourseEnrollment enrollmentQuery = new EduCourseEnrollment();
        enrollmentQuery.setStudentUserId(child.getStudentUserId());
        List<EduCourseEnrollment> enrollments = enrollmentService.selectEnrollmentList(enrollmentQuery);

        EduHomeworkQuestion questionQuery = new EduHomeworkQuestion();
        questionQuery.setStudentUserId(child.getStudentUserId());
        List<EduHomeworkQuestion> questions = questionService.selectQuestionList(questionQuery);

        StringBuilder prompt = new StringBuilder();
        prompt.append("学生：").append(child.getStudentName())
                .append("，年级班级：").append(StringUtils.defaultString(child.getGradeName(), "未填写"))
                .append(StringUtils.defaultString(child.getClassName(), ""))
                .append("，兴趣标签：").append(StringUtils.defaultString(child.getInterestTags(), "未填写"))
                .append("\n");
        prompt.append("家长补充关注：").append(StringUtils.defaultString(body.getConcern(), "未填写")).append("\n");
        prompt.append("学习记录：\n");
        for (EduCourseEnrollment item : enrollments)
        {
            prompt.append("- ").append(item.getCourseName())
                    .append("：").append(StringUtils.defaultString(item.getLearningRecord(), "暂无记录"))
                    .append("；互动：").append(StringUtils.defaultString(item.getInteractionSummary(), "暂无"))
                    .append("\n");
        }
        prompt.append("作业问答情况：\n");
        for (EduHomeworkQuestion item : questions)
        {
            prompt.append("- ").append(item.getQuestionTitle())
                    .append("：").append(StringUtils.defaultString(item.getQuestionContent(), ""))
                    .append("；AI答复摘要：").append(StringUtils.defaultString(item.getAiAnswer(), "暂无"))
                    .append("\n");
        }
        return prompt.toString();
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
            String reason = line.substring(matcher.end()).replaceAll("^[，；：\\-\\s]+", "").trim();
            JSONObject object = new JSONObject();
            object.put("title", StringUtils.isEmpty(title) ? "推荐资源" : title);
            object.put("link", link);
            object.put("reason", StringUtils.isEmpty(reason) ? "与当前学习兴趣较匹配" : reason);
            result.add(object);
        }
        return result;
    }

    private List<JSONObject> buildLocalRecommendations(String interest, List<OnlineResourceItem> resources)
    {
        List<JSONObject> result = new ArrayList<>();
        if (resources == null || resources.isEmpty())
        {
            return result;
        }

        String normalizedInterest = StringUtils.defaultString(interest).toLowerCase(Locale.ROOT);
        String[] keywords = normalizedInterest.split("[,，、\\s]+");
        List<JSONObject> matched = new ArrayList<>();
        List<JSONObject> secondary = new ArrayList<>();

        for (OnlineResourceItem item : resources)
        {
            if (item == null || StringUtils.isEmpty(item.getTitle()) || StringUtils.isEmpty(item.getLink()))
            {
                continue;
            }

            String resourceText = (StringUtils.defaultString(item.getTitle()) + " "
                    + StringUtils.defaultString(item.getCategory()) + " "
                    + StringUtils.defaultString(item.getDescription()) + " "
                    + StringUtils.defaultString(item.getSource())).toLowerCase(Locale.ROOT);

            int score = 0;
            for (String keyword : keywords)
            {
                String current = StringUtils.trim(keyword);
                if (StringUtils.isNotEmpty(current) && resourceText.contains(current))
                {
                    score++;
                }
            }

            JSONObject object = new JSONObject();
            object.put("title", item.getTitle());
            object.put("link", item.getLink());
            object.put("reason", score > 0
                    ? "与“" + interest + "”较匹配，可优先从该资源开始学习。"
                    : "属于当前资源库中较通用的优质学习入口，可作为延伸学习参考。");

            if (score > 0)
            {
                matched.add(object);
            }
            else
            {
                secondary.add(object);
            }
        }

        result.addAll(matched);
        for (JSONObject item : secondary)
        {
            if (result.size() >= 4)
            {
                break;
            }
            result.add(item);
        }
        if (result.size() > 4)
        {
            return new ArrayList<>(result.subList(0, 4));
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
