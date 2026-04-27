package com.eapple.system.service.impl.edu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.domain.edu.EduAiLog;
import com.eapple.system.domain.edu.EduCourse;
import com.eapple.system.domain.edu.EduCourseEnrollment;
import com.eapple.system.domain.edu.EduStudentProfile;
import com.eapple.system.mapper.edu.EduCourseMapper;
import com.eapple.system.mapper.edu.EduEnrollmentMapper;
import com.eapple.system.mapper.edu.EduStudentProfileMapper;
import com.eapple.system.service.edu.IEduAiLogService;
import com.eapple.system.service.edu.IEduAiService;
import com.eapple.system.service.edu.IEduCourseService;

@Service
public class EduCourseServiceImpl implements IEduCourseService
{
    @Autowired
    private EduCourseMapper courseMapper;

    @Autowired
    private EduEnrollmentMapper enrollmentMapper;

    @Autowired
    private EduStudentProfileMapper profileMapper;

    @Autowired
    private IEduAiService aiService;

    @Autowired
    private IEduAiLogService aiLogService;

    @Override
    public EduCourse selectCourseById(Long courseId)
    {
        return courseMapper.selectCourseById(courseId);
    }

    @Override
    public List<EduCourse> selectCourseList(EduCourse course)
    {
        fillQueryScope(course, false);
        return courseMapper.selectCourseList(course);
    }

    @Override
    public List<EduCourse> selectMyCourseList()
    {
        EduCourse course = new EduCourse();
        fillQueryScope(course, true);
        return courseMapper.selectCourseList(course);
    }

    @Override
    public int insertCourse(EduCourse course)
    {
        if (!SecurityUtils.hasExactRole("edu_teacher") && !SecurityUtils.isAdmin())
        {
            throw new ServiceException("只有教师或管理员可以发布课程");
        }
        course.setTeacherUserId(SecurityUtils.getUserId());
        if (StringUtils.isEmpty(course.getTeacherName()))
        {
            course.setTeacherName(SecurityUtils.getLoginUser().getUser().getNickName());
        }
        course.setCurrentCapacity(0);
        course.setCreateBy(SecurityUtils.getUsername());
        return courseMapper.insertCourse(course);
    }

    @Override
    public int updateCourse(EduCourse course)
    {
        EduCourse dbCourse = ensureCourseExists(course.getCourseId());
        ensureCourseOwner(dbCourse);
        course.setUpdateBy(SecurityUtils.getUsername());
        return courseMapper.updateCourse(course);
    }

    @Override
    public int deleteCourseByIds(Long[] courseIds)
    {
        for (Long courseId : courseIds)
        {
            EduCourse course = ensureCourseExists(courseId);
            if (!"1".equals(course.getStatus()))
            {
                throw new ServiceException("课程需要先停开后才能删除");
            }
            if (!SecurityUtils.isAdmin())
            {
                ensureCourseOwner(course);
            }
        }
        return courseMapper.deleteCourseByIds(courseIds);
    }

    @Override
    public int enrollCourse(Long courseId, Long studentUserId)
    {
        EduCourse course = ensureCourseExists(courseId);
        if (!"0".equals(course.getStatus()))
        {
            throw new ServiceException("当前课程未开放报名");
        }
        if (course.getEndDate() != null && course.getEndDate().before(todayStart()))
        {
            throw new ServiceException("课程已结课，暂不支持报名");
        }

        Long targetStudentId = resolveStudentUserId(studentUserId);
        EduStudentProfile profile = profileMapper.selectProfileByStudentUserId(targetStudentId);
        if (profile == null)
        {
            throw new ServiceException("请先维护学生档案后再报名");
        }
        if (course.getCurrentCapacity() != null && course.getMaxCapacity() != null
                && course.getCurrentCapacity() >= course.getMaxCapacity())
        {
            throw new ServiceException("课程名额已满");
        }
        if (enrollmentMapper.selectEnrollmentByCourseAndStudent(courseId, targetStudentId) != null)
        {
            throw new ServiceException("该学生已报名此课程");
        }

        EduCourseEnrollment enrollment = new EduCourseEnrollment();
        enrollment.setCourseId(courseId);
        enrollment.setCourseName(course.getCourseName());
        enrollment.setStudentUserId(profile.getStudentUserId());
        enrollment.setStudentName(profile.getStudentName());
        enrollment.setParentUserId(profile.getParentUserId());
        enrollment.setParentName(profile.getParentName());
        enrollment.setTeacherUserId(course.getTeacherUserId());
        enrollment.setTeacherName(course.getTeacherName());
        enrollment.setStatus("0");
        enrollment.setSignupSource(SecurityUtils.hasExactRole("edu_parent") ? "parent" : "student");
        enrollment.setCreateBy(SecurityUtils.getUsername());

        int rows = enrollmentMapper.insertEnrollment(enrollment);
        enrollmentMapper.increaseCourseCapacity(courseId);
        return rows;
    }

    @Override
    public int cancelEnrollment(Long courseId, Long studentUserId)
    {
        ensureCourseExists(courseId);
        Long targetStudentId = resolveStudentUserId(studentUserId);
        EduCourseEnrollment enrollment = enrollmentMapper.selectEnrollmentByCourseAndStudent(courseId, targetStudentId);
        if (enrollment == null)
        {
            throw new ServiceException("当前学生未报名该课程");
        }
        if ("1".equals(enrollment.getStatus()))
        {
            throw new ServiceException("该课程报名已进入学习阶段，暂不支持取消");
        }
        int rows = enrollmentMapper.deleteEnrollmentById(enrollment.getEnrollmentId());
        if (rows > 0)
        {
            enrollmentMapper.decreaseCourseCapacity(courseId);
        }
        return rows;
    }

    @Override
    public String generateCourseNotice(Long courseId)
    {
        EduCourse course = ensureCourseExists(courseId);
        ensureCourseOwner(course);
        String prompt = "课程名称：" + course.getCourseName()
                + "\n分类：" + course.getCategory()
                + "\n教师：" + course.getTeacherName()
                + "\n时间：" + course.getWeekDay()
                + "\n地点：" + course.getCampus()
                + "\n课程简介：" + StringUtils.defaultString(course.getDescription());
        String content = aiService.generateCourseNotice(courseId, prompt);
        courseMapper.updateAiNotice(courseId, content);
        return content;
    }

    @Override
    public String generateTeachingSuggestion(Long courseId)
    {
        EduCourse course = ensureCourseExists(courseId);
        ensureCourseOwner(course);
        String prompt = "课程名称：" + course.getCourseName()
                + "\n面向对象：中小学课后服务学生"
                + "\n课程简介：" + StringUtils.defaultString(course.getDescription())
                + "\n当前报名人数：" + String.valueOf(course.getCurrentCapacity());
        String content = aiService.generateTeachingSuggestion(courseId, prompt);
        courseMapper.updateAiSuggestion(courseId, content);
        return content;
    }

    @Override
    public List<EduCourse> recommendCoursesByStudent(Long studentUserId)
    {
        Long targetStudentId = resolveStudentUserId(studentUserId);
        EduStudentProfile profile = profileMapper.selectProfileByStudentUserId(targetStudentId);
        if (profile == null)
        {
            throw new ServiceException("请先完善学生档案后再获取课程推荐");
        }

        EduCourse query = new EduCourse();
        query.setStatus("0");
        List<EduCourse> allCourses = courseMapper.selectCourseList(query);
        List<EduCourse> recommendations = new ArrayList<>();
        Set<String> interestKeywords = extractKeywords(profile.getInterestTags());

        for (EduCourse course : allCourses)
        {
            if (course.getEndDate() != null && course.getEndDate().before(todayStart()))
            {
                continue;
            }
            if (enrollmentMapper.selectEnrollmentByCourseAndStudent(course.getCourseId(), targetStudentId) != null)
            {
                continue;
            }
            if (course.getMaxCapacity() != null && course.getCurrentCapacity() != null
                    && course.getCurrentCapacity() >= course.getMaxCapacity())
            {
                continue;
            }

            int score = calculateRecommendationScore(course, profile, interestKeywords);
            if (score <= 0)
            {
                continue;
            }
            course.setRecommendationScore(score);
            recommendations.add(course);
        }

        recommendations.sort(Comparator.comparing(EduCourse::getRecommendationScore, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(EduCourse::getEnrollCount, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(EduCourse::getCourseId, Comparator.nullsLast(Comparator.reverseOrder())));

        List<EduCourse> topRecommendations = recommendations.size() > 4
                ? new ArrayList<>(recommendations.subList(0, 4)) : recommendations;
        fillRecommendationReasons(topRecommendations, profile, interestKeywords);
        recordCourseRecommendationLog(profile, topRecommendations);
        return topRecommendations;
    }

    private void fillQueryScope(EduCourse course, boolean onlyMine)
    {
        if (SecurityUtils.hasExactRole("edu_teacher"))
        {
            course.setTeacherUserId(SecurityUtils.getUserId());
        }
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            course.getParams().put("currentUserId", SecurityUtils.getUserId());
            course.getParams().put("currentRole", "student");
            course.getParams().put("onlyMine", onlyMine);
            if (!onlyMine)
            {
                course.setStatus("0");
            }
        }
        if (SecurityUtils.hasExactRole("edu_parent"))
        {
            course.getParams().put("currentUserId", SecurityUtils.getUserId());
            course.getParams().put("currentRole", "parent");
            course.getParams().put("onlyMine", onlyMine);
            if (!onlyMine)
            {
                course.setStatus("0");
            }
        }
    }

    private EduCourse ensureCourseExists(Long courseId)
    {
        EduCourse course = courseMapper.selectCourseById(courseId);
        if (course == null)
        {
            throw new ServiceException("课程不存在");
        }
        return course;
    }

    private Date todayStart()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private void ensureCourseOwner(EduCourse course)
    {
        if (!SecurityUtils.isAdmin() && SecurityUtils.hasExactRole("edu_teacher")
                && !SecurityUtils.getUserId().equals(course.getTeacherUserId()))
        {
            throw new ServiceException("只能操作本人发布的课程");
        }
    }

    private Long resolveStudentUserId(Long studentUserId)
    {
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            Long currentUserId = SecurityUtils.getUserId();
            if (studentUserId != null && !currentUserId.equals(studentUserId))
            {
                throw new ServiceException("学生只能为自己报名");
            }
            return currentUserId;
        }

        if (SecurityUtils.hasExactRole("edu_parent"))
        {
            if (studentUserId == null)
            {
                throw new ServiceException("家长报名时必须选择孩子");
            }
            EduStudentProfile profile = profileMapper.selectProfileByStudentUserId(studentUserId);
            if (profile == null || !SecurityUtils.getUserId().equals(profile.getParentUserId()))
            {
                throw new ServiceException("只能为已关联的孩子报名");
            }
            return studentUserId;
        }
        return studentUserId;
    }

    private int calculateRecommendationScore(EduCourse course, EduStudentProfile profile, Set<String> interestKeywords)
    {
        int score = 20;
        String courseText = (StringUtils.defaultString(course.getCourseName()) + " "
                + StringUtils.defaultString(course.getCategory()) + " "
                + StringUtils.defaultString(course.getDescription())).toLowerCase(Locale.ROOT);

        for (String keyword : interestKeywords)
        {
            if (courseText.contains(keyword))
            {
                score += 18;
            }
        }

        if (StringUtils.isNotEmpty(profile.getGradeName()) && StringUtils.isNotEmpty(course.getDescription())
                && course.getDescription().contains(profile.getGradeName()))
        {
            score += 10;
        }
        if (course.getEnrollCount() != null)
        {
            score += Math.min(course.getEnrollCount().intValue() * 3, 15);
        }
        if (course.getMaxCapacity() != null && course.getCurrentCapacity() != null)
        {
            int remain = course.getMaxCapacity() - course.getCurrentCapacity();
            if (remain > 0)
            {
                score += Math.min(remain, 8);
            }
        }
        return score;
    }

    private Set<String> extractKeywords(String interestTags)
    {
        Set<String> keywords = new LinkedHashSet<>();
        if (StringUtils.isEmpty(interestTags))
        {
            return keywords;
        }
        String normalized = interestTags.replace("，", ",").replace("、", ",").replace("；", ",").replace(";", ",");
        for (String token : normalized.split(","))
        {
            String trimmed = token == null ? "" : token.trim().toLowerCase(Locale.ROOT);
            if (!trimmed.isEmpty())
            {
                keywords.add(trimmed);
            }
        }
        return keywords;
    }

    private String buildRecommendationReason(EduCourse course, EduStudentProfile profile)
    {
        String prompt = "学生姓名：" + profile.getStudentName()
                + "\n年级班级：" + StringUtils.defaultString(profile.getGradeName()) + " " + StringUtils.defaultString(profile.getClassName())
                + "\n兴趣标签：" + StringUtils.defaultString(profile.getInterestTags(), "暂无")
                + "\n课程名称：" + course.getCourseName()
                + "\n课程分类：" + StringUtils.defaultString(course.getCategory(), "未分类")
                + "\n课程简介：" + StringUtils.defaultString(course.getDescription(), "暂无简介");
        String aiReason = aiService.generateCourseRecommendation(profile.getStudentUserId(), prompt);
        return sanitizeRecommendationReason(aiReason, profile);
    }

    private String sanitizeRecommendationReason(String aiReason, EduStudentProfile profile)
    {
        String cleaned = StringUtils.defaultString(aiReason)
                .replace("\r", "")
                .replace("```markdown", "")
                .replace("```", "")
                .replace("**", "")
                .replace("user", "")
                .replace("assistant", "")
                .replace("system", "")
                .replaceAll("[A-Za-z]{3,}", "")
                .replaceAll("\\s+", " ")
                .trim();

        String[] fragments = cleaned.split("[。！？?!\n]");
        for (String fragment : fragments)
        {
            String candidate = fragment == null ? "" : fragment.trim();
            if (candidate.length() >= 6)
            {
                return ensureSentence(candidate);
            }
        }

        String interests = StringUtils.defaultString(profile.getInterestTags(), "当前兴趣");
        String firstInterest = interests.split("[，、;；\\s]+")[0];
        return ensureSentence("课程内容与" + firstInterest + "方向较匹配，建议优先关注");
    }

    private void fillRecommendationReasons(List<EduCourse> courses, EduStudentProfile profile, Set<String> interestKeywords)
    {
        for (EduCourse course : courses)
        {
            course.setRecommendationReason(buildLocalRecommendationReason(course, profile, interestKeywords));
        }
    }

    private String buildLocalRecommendationReason(EduCourse course, EduStudentProfile profile, Set<String> interestKeywords)
    {
        String matchedKeyword = findMatchedKeyword(course, interestKeywords);
        String gradeName = StringUtils.defaultString(profile.getGradeName(), "当前年级");
        String courseName = StringUtils.defaultString(course.getCourseName(), "这门课程");
        String category = StringUtils.defaultString(course.getCategory(), "综合素养");
        if (StringUtils.isNotEmpty(matchedKeyword))
        {
            return ensureSentence(courseName + "与孩子的“" + matchedKeyword + "”兴趣方向匹配，可在" + category + "学习中保持较高参与度");
        }
        if (StringUtils.isNotEmpty(profile.getInterestTags()))
        {
            return ensureSentence(courseName + "能承接孩子已有兴趣，适合作为" + gradeName + "阶段的课后拓展与持续练习");
        }
        return ensureSentence(courseName + "课程节奏和内容较适合" + gradeName + "学生，可用于补充课后服务中的实践体验");
    }

    private String findMatchedKeyword(EduCourse course, Set<String> interestKeywords)
    {
        String courseText = (StringUtils.defaultString(course.getCourseName()) + " "
                + StringUtils.defaultString(course.getCategory()) + " "
                + StringUtils.defaultString(course.getDescription())).toLowerCase(Locale.ROOT);
        for (String keyword : interestKeywords)
        {
            if (courseText.contains(keyword))
            {
                return keyword;
            }
        }
        return "";
    }

    private void recordCourseRecommendationLog(EduStudentProfile profile, List<EduCourse> recommendations)
    {
        if (recommendations == null || recommendations.isEmpty())
        {
            return;
        }
        EduAiLog log = new EduAiLog();
        log.setBusinessType("course_recommendation");
        log.setBizId(profile.getStudentUserId());
        log.setUserId(SecurityUtils.getUserId());
        log.setUserName(SecurityUtils.getUsername());
        log.setRoleType(resolveRoleType());
        log.setPromptContent(buildCourseRecommendationPrompt(profile, recommendations));
        log.setResponseContent(buildCourseRecommendationResponse(profile, recommendations));
        log.setModelName("QINGHE-Recommendation");
        log.setStatus("success");
        log.setRiskFlag("normal");
        log.setPromptTokens(log.getPromptContent().length());
        log.setCompletionTokens(log.getResponseContent().length());
        log.setLatencyMs(0L);
        aiLogService.insertAiLog(log);
    }

    private String buildCourseRecommendationPrompt(EduStudentProfile profile, List<EduCourse> recommendations)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("学生：").append(StringUtils.defaultString(profile.getStudentName())).append("\n");
        builder.append("年级班级：").append(StringUtils.defaultString(profile.getGradeName())).append(" ")
                .append(StringUtils.defaultString(profile.getClassName())).append("\n");
        builder.append("兴趣标签：").append(StringUtils.defaultString(profile.getInterestTags(), "暂未填写")).append("\n");
        builder.append("本次候选课程：");
        for (EduCourse course : recommendations)
        {
            builder.append("\n- ").append(StringUtils.defaultString(course.getCourseName()))
                    .append("（").append(StringUtils.defaultString(course.getCategory(), "未分类")).append("）")
                    .append("，推荐分：").append(course.getRecommendationScore());
        }
        return builder.toString();
    }

    private String buildCourseRecommendationResponse(EduStudentProfile profile, List<EduCourse> recommendations)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("本次已结合").append(StringUtils.defaultString(profile.getStudentName(), "学生"))
                .append("的年级、兴趣标签、课程容量和报名情况生成课程推荐。\n");
        for (int i = 0; i < recommendations.size(); i++)
        {
            EduCourse course = recommendations.get(i);
            builder.append(i + 1).append(". ").append(StringUtils.defaultString(course.getCourseName()))
                    .append("：").append(StringUtils.defaultString(course.getRecommendationReason())).append("\n");
        }
        builder.append("建议家长优先选择孩子兴趣匹配度高、上课时间稳定且能持续完成学习记录的课程。");
        return builder.toString();
    }

    private String resolveRoleType()
    {
        if (SecurityUtils.isAdmin())
        {
            return "admin";
        }
        if (SecurityUtils.hasExactRole("edu_teacher"))
        {
            return "edu_teacher";
        }
        if (SecurityUtils.hasExactRole("edu_parent"))
        {
            return "edu_parent";
        }
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            return "edu_student";
        }
        return "user";
    }

    private String ensureSentence(String text)
    {
        String sentence = StringUtils.defaultString(text).trim();
        if (StringUtils.isEmpty(sentence))
        {
            return "课程内容与学生兴趣较匹配，建议优先关注。";
        }
        if (!sentence.endsWith("。"))
        {
            sentence = sentence + "。";
        }
        return sentence;
    }
}
