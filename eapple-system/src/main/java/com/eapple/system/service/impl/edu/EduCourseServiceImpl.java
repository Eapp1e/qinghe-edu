package com.eapple.system.service.impl.edu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.mapper.SysUserMapper;
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
    private static final Pattern COURSE_SLOT_PATTERN = Pattern.compile("(周[一二三四五六日天])\\s*(\\d{1,2}:\\d{2})?\\s*-?\\s*(\\d{1,2}:\\d{2})?");

    @Autowired
    private EduCourseMapper courseMapper;

    @Autowired
    private EduEnrollmentMapper enrollmentMapper;

    @Autowired
    private EduStudentProfileMapper profileMapper;

    @Autowired
    private SysUserMapper userMapper;

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
        bindTeacherCategory(course);
        normalizeCourseCategory(course);
        normalizeGradeScope(course);
        ensureTeacherTypeAllowed(course);
        ensureCourseCode(course);
        course.setCurrentCapacity(0);
        course.setCreateBy(SecurityUtils.getUsername());
        ensureTeacherScheduleAvailable(course);
        return courseMapper.insertCourse(course);
    }

    @Override
    public int updateCourse(EduCourse course)
    {
        EduCourse dbCourse = ensureCourseExists(course.getCourseId());
        ensureCourseOwner(dbCourse);
        bindTeacherCategory(course);
        normalizeCourseCategory(course);
        normalizeGradeScope(course);
        EduCourse mergedCourse = mergeCourseForSchedule(dbCourse, course);
        ensureTeacherTypeAllowed(mergedCourse);
        ensureTeacherScheduleAvailable(mergedCourse);
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
        if (!matchesGradeScope(course, profile.getGradeName()))
        {
            throw new ServiceException("该课程开课范围与学生年级不匹配，请选择适合当前年级的课程");
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

        ensureNoScheduleConflict(course, targetStudentId);

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
        String gradeStage = resolveGradeStage(profile.getGradeName());

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
            if (hasScheduleConflict(course, targetStudentId))
            {
                continue;
            }
            if (!matchesGradeStage(course, gradeStage) || !matchesGradeScope(course, profile.getGradeName()))
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
            EduStudentProfile profile = profileMapper.selectProfileByStudentUserId(SecurityUtils.getUserId());
            applyGradeStageParam(course, profile);
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
            EduStudentProfile profileQuery = new EduStudentProfile();
            profileQuery.setParentUserId(SecurityUtils.getUserId());
            List<EduStudentProfile> children = profileMapper.selectProfileList(profileQuery);
            applyParentGradeStageParam(course, children);
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

    private void ensureCourseCode(EduCourse course)
    {
        if (StringUtils.isNotEmpty(course.getCourseCode()))
        {
            course.setCourseCode(StringUtils.trim(course.getCourseCode()));
            return;
        }
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        course.setCourseCode("QH-C" + timestamp);
    }

    private void applyGradeStageParam(EduCourse course, EduStudentProfile profile)
    {
        String gradeStage = profile == null ? "" : resolveGradeStage(profile.getGradeName());
        if (StringUtils.isNotEmpty(gradeStage))
        {
            course.getParams().put("gradeStage", gradeStage);
        }
        if (profile != null && StringUtils.isNotEmpty(profile.getGradeName()))
        {
            course.getParams().put("studentGrade", profile.getGradeName());
        }
    }

    private void applyParentGradeStageParam(EduCourse course, List<EduStudentProfile> children)
    {
        String stage = "";
        if (children == null || children.isEmpty())
        {
            return;
        }
        if (children.size() == 1 && StringUtils.isNotEmpty(children.get(0).getGradeName()))
        {
            course.getParams().put("studentGrade", children.get(0).getGradeName());
        }
        for (EduStudentProfile child : children)
        {
            String childStage = resolveGradeStage(child.getGradeName());
            if (StringUtils.isEmpty(childStage))
            {
                return;
            }
            if (StringUtils.isEmpty(stage))
            {
                stage = childStage;
            }
            else if (!stage.equals(childStage))
            {
                return;
            }
        }
        course.getParams().put("gradeStage", stage);
    }

    private void bindTeacherCategory(EduCourse course)
    {
        if (!SecurityUtils.hasExactRole("edu_teacher"))
        {
            return;
        }
        SysUser teacher = userMapper.selectUserById(SecurityUtils.getUserId());
        String teacherType = teacher == null ? "" : StringUtils.defaultString(teacher.getTeacherType());
        String inferredType = resolveRequiredTeacherType(course);
        String selectedType = teacherTypeMatches(teacherType, inferredType) ? inferredType : firstTeacherType(teacherType);
        String category = categoryByTeacherType(selectedType);
        if (StringUtils.isNotEmpty(category))
        {
            course.setCategory(category);
        }
    }

    private String firstTeacherType(String teacherType)
    {
        for (String item : StringUtils.defaultString(teacherType).split(","))
        {
            if (StringUtils.isNotEmpty(StringUtils.trim(item)))
            {
                return StringUtils.trim(item);
            }
        }
        return "general";
    }

    private String categoryByTeacherType(String teacherType)
    {
        if ("science".equals(teacherType))
        {
            return "理科拓展";
        }
        if ("humanities".equals(teacherType))
        {
            return "文科阅读";
        }
        if ("art".equals(teacherType))
        {
            return "美育实践";
        }
        if ("sports".equals(teacherType))
        {
            return "体育健康";
        }
        if ("computer".equals(teacherType))
        {
            return "计算机编程";
        }
        if ("general".equals(teacherType))
        {
            return "综合素养";
        }
        return "";
    }

    private void normalizeCourseCategory(EduCourse course)
    {
        String category = StringUtils.defaultString(course.getCategory()).trim();
        if (StringUtils.isEmpty(category) || category.contains("?"))
        {
            course.setCategory("综合素养");
            return;
        }
        if ("学科提升".equals(category) || "科学实验".equals(category))
        {
            course.setCategory("理科拓展");
            return;
        }
        if ("科技创新".equals(category) || "编程思维".equals(category))
        {
            course.setCategory("计算机编程");
            return;
        }
        if ("传统文化".equals(category))
        {
            course.setCategory("文科阅读");
            return;
        }
        if ("劳动实践".equals(category))
        {
            course.setCategory("综合素养");
            return;
        }
        course.setCategory(category);
    }

    private void normalizeGradeScope(EduCourse course)
    {
        if (StringUtils.isNotEmpty(course.getGradeScope()))
        {
            course.setGradeScope(StringUtils.defaultString(course.getGradeScope()).replace("，", ",").replace(" ", ""));
            return;
        }
        String text = StringUtils.defaultString(course.getCourseName()) + " "
                + StringUtils.defaultString(course.getDescription()) + " "
                + StringUtils.defaultString(course.getRemark());
        if (containsAny(text, "七年级", "八年级", "九年级", "初中", "中考"))
        {
            course.setGradeScope("七年级,八年级,九年级");
            return;
        }
        if (containsAny(text, "少儿", "小学", "一年级", "二年级", "三年级", "四年级", "五年级", "六年级", "启蒙", "绘本"))
        {
            course.setGradeScope("一年级,二年级,三年级,四年级,五年级,六年级");
            return;
        }
        course.setGradeScope("一年级,二年级,三年级,四年级,五年级,六年级,七年级,八年级,九年级");
    }

    private boolean matchesGradeScope(EduCourse course, String gradeName)
    {
        String grade = StringUtils.defaultString(gradeName).trim();
        String scope = StringUtils.defaultString(course.getGradeScope()).replace("，", ",").replace(" ", "");
        if (StringUtils.isEmpty(grade) || StringUtils.isEmpty(scope))
        {
            return matchesGradeStage(course, resolveGradeStage(gradeName));
        }
        for (String item : scope.split(","))
        {
            if (grade.equals(item))
            {
                return true;
            }
        }
        return false;
    }

    private void ensureTeacherTypeAllowed(EduCourse course)
    {
        if (course.getTeacherUserId() == null)
        {
            throw new ServiceException("请先选择授课教师");
        }
        SysUser teacher = userMapper.selectUserById(course.getTeacherUserId());
        String teacherType = teacher == null ? "" : StringUtils.defaultString(teacher.getTeacherType());
        if (StringUtils.isEmpty(teacherType))
        {
            teacherType = inferTeacherTypeFromText(StringUtils.defaultString(teacher == null ? "" : teacher.getRemark()) + " "
                    + StringUtils.defaultString(teacher == null ? "" : teacher.getNickName()));
        }
        String requiredType = resolveRequiredTeacherType(course);
        if (StringUtils.isEmpty(requiredType) || StringUtils.isEmpty(teacherType))
        {
            return;
        }
        if (!teacherTypeMatches(teacherType, requiredType))
        {
            throw new ServiceException("该课程分类需要“" + teacherTypeName(requiredType) + "”教师，当前教师类型为“"
                    + teacherTypeName(teacherType) + "”，请调整教师或课程分类");
        }
    }

    private boolean teacherTypeMatches(String teacherType, String requiredType)
    {
        for (String item : StringUtils.defaultString(teacherType).split(","))
        {
            if (requiredType.equals(item.trim()))
            {
                return true;
            }
        }
        return false;
    }

    private String resolveRequiredTeacherType(EduCourse course)
    {
        String text = StringUtils.defaultString(course.getCategory()) + " "
                + StringUtils.defaultString(course.getCourseName()) + " "
                + StringUtils.defaultString(course.getDescription());
        if (containsAny(text, "体育", "足球", "篮球", "羽毛球", "体能", "运动"))
        {
            return "sports";
        }
        if (containsAny(text, "美育", "艺术", "美术", "舞蹈", "音乐", "书法", "陶艺"))
        {
            return "art";
        }
        if (containsAny(text, "计算机", "编程", "Python", "机器人", "人工智能", "算法"))
        {
            return "computer";
        }
        if (containsAny(text, "数学", "科学", "物理", "化学", "生物", "实验", "理科"))
        {
            return "science";
        }
        if (containsAny(text, "语文", "阅读", "写作", "英语", "表达", "文科", "传统文化", "诵读", "主持"))
        {
            return "humanities";
        }
        if (containsAny(text, "劳动", "手作", "非遗", "成长支持", "心理", "时间管理"))
        {
            return "general";
        }
        return "general";
    }

    private String inferTeacherTypeFromText(String text)
    {
        if (containsAny(text, "体育", "运动"))
        {
            return "sports";
        }
        if (containsAny(text, "音乐", "美术", "艺术", "舞蹈"))
        {
            return "art";
        }
        if (containsAny(text, "计算机", "编程", "机器人"))
        {
            return "computer";
        }
        if (containsAny(text, "数学", "科学", "物理", "理科"))
        {
            return "science";
        }
        if (containsAny(text, "语文", "阅读", "英语", "语言", "文科"))
        {
            return "humanities";
        }
        return "general";
    }

    private String teacherTypeName(String type)
    {
        if (StringUtils.defaultString(type).contains(","))
        {
            List<String> names = new ArrayList<>();
            for (String item : type.split(","))
            {
                names.add(teacherTypeName(item.trim()));
            }
            return String.join("、", names);
        }
        if ("science".equals(type))
        {
            return "理科";
        }
        if ("humanities".equals(type))
        {
            return "文科";
        }
        if ("art".equals(type))
        {
            return "美育";
        }
        if ("sports".equals(type))
        {
            return "体育";
        }
        if ("computer".equals(type))
        {
            return "计算机";
        }
        return "综合实践";
    }

    private String resolveGradeStage(String gradeName)
    {
        String grade = StringUtils.defaultString(gradeName);
        if (grade.contains("七") || grade.contains("八") || grade.contains("九"))
        {
            return "middle";
        }
        if (grade.contains("一") || grade.contains("二") || grade.contains("三")
                || grade.contains("四") || grade.contains("五") || grade.contains("六"))
        {
            return "elementary";
        }
        return "";
    }

    private boolean matchesGradeStage(EduCourse course, String gradeStage)
    {
        if (StringUtils.isEmpty(gradeStage))
        {
            return true;
        }
        String text = (StringUtils.defaultString(course.getCourseName()) + " "
                + StringUtils.defaultString(course.getCategory()) + " "
                + StringUtils.defaultString(course.getDescription()) + " "
                + StringUtils.defaultString(course.getRemark()));
        boolean middleOnly = containsAny(text, "初中", "七年级", "八年级", "九年级", "中考");
        boolean elementaryOnly = containsAny(text, "小学", "一年级", "二年级", "三年级", "四年级", "五年级", "六年级", "少儿", "小小", "启蒙", "绘本");
        if ("elementary".equals(gradeStage))
        {
            return !middleOnly;
        }
        if ("middle".equals(gradeStage))
        {
            return !elementaryOnly;
        }
        return true;
    }

    private boolean containsAny(String text, String... keywords)
    {
        for (String keyword : keywords)
        {
            if (text.contains(keyword))
            {
                return true;
            }
        }
        return false;
    }

    private void ensureNoScheduleConflict(EduCourse course, Long studentUserId)
    {
        EduCourseEnrollment query = new EduCourseEnrollment();
        query.setStudentUserId(studentUserId);
        List<EduCourseEnrollment> enrollments = enrollmentMapper.selectEnrollmentList(query);
        for (EduCourseEnrollment enrollment : enrollments)
        {
            if (course.getCourseId().equals(enrollment.getCourseId()))
            {
                continue;
            }
            if (isScheduleConflict(course, enrollment))
            {
                throw new ServiceException("该学生已报名《" + enrollment.getCourseName() + "》，上课时间与当前课程冲突");
            }
        }
    }

    private boolean hasScheduleConflict(EduCourse course, Long studentUserId)
    {
        EduCourseEnrollment query = new EduCourseEnrollment();
        query.setStudentUserId(studentUserId);
        List<EduCourseEnrollment> enrollments = enrollmentMapper.selectEnrollmentList(query);
        for (EduCourseEnrollment enrollment : enrollments)
        {
            if (course.getCourseId().equals(enrollment.getCourseId()))
            {
                continue;
            }
            if (isScheduleConflict(course, enrollment))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isScheduleConflict(EduCourse course, EduCourseEnrollment enrollment)
    {
        if (!isDateRangeOverlap(course.getStartDate(), course.getEndDate(), enrollment.getStartDate(), enrollment.getEndDate()))
        {
            return false;
        }
        List<CourseSlot> courseSlots = parseCourseSlots(course.getWeekDay(), course.getStartTime(), course.getEndTime());
        List<CourseSlot> enrolledSlots = parseCourseSlots(enrollment.getWeekDay(), enrollment.getStartTime(), enrollment.getEndTime());
        for (CourseSlot courseSlot : courseSlots)
        {
            for (CourseSlot enrolledSlot : enrolledSlots)
            {
                if (courseSlot.weekDay.equals(enrolledSlot.weekDay)
                        && isTimeOverlap(courseSlot.startTime, courseSlot.endTime, enrolledSlot.startTime, enrolledSlot.endTime))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void ensureTeacherScheduleAvailable(EduCourse course)
    {
        if (course.getTeacherUserId() == null)
        {
            return;
        }
        EduCourse query = new EduCourse();
        query.setTeacherUserId(course.getTeacherUserId());
        query.setStatus("0");
        List<EduCourse> teacherCourses = courseMapper.selectCourseList(query);
        for (EduCourse teacherCourse : teacherCourses)
        {
            if (course.getCourseId() != null && course.getCourseId().equals(teacherCourse.getCourseId()))
            {
                continue;
            }
            if (isCourseScheduleConflict(course, teacherCourse))
            {
                throw new ServiceException("授课教师同时间已安排《" + teacherCourse.getCourseName() + "》，请调整上课时间");
            }
        }
    }

    private EduCourse mergeCourseForSchedule(EduCourse dbCourse, EduCourse course)
    {
        EduCourse merged = new EduCourse();
        merged.setCourseId(dbCourse.getCourseId());
        merged.setCourseName(StringUtils.defaultString(course.getCourseName(), dbCourse.getCourseName()));
        merged.setCategory(StringUtils.defaultString(course.getCategory(), dbCourse.getCategory()));
        merged.setGradeScope(StringUtils.defaultString(course.getGradeScope(), dbCourse.getGradeScope()));
        merged.setTeacherUserId(course.getTeacherUserId() == null ? dbCourse.getTeacherUserId() : course.getTeacherUserId());
        merged.setTeacherName(StringUtils.defaultString(course.getTeacherName(), dbCourse.getTeacherName()));
        merged.setCampus(StringUtils.defaultString(course.getCampus(), dbCourse.getCampus()));
        merged.setWeekDay(StringUtils.defaultString(course.getWeekDay(), dbCourse.getWeekDay()));
        merged.setStartTime(StringUtils.defaultString(course.getStartTime(), dbCourse.getStartTime()));
        merged.setEndTime(StringUtils.defaultString(course.getEndTime(), dbCourse.getEndTime()));
        merged.setStartDate(course.getStartDate() == null ? dbCourse.getStartDate() : course.getStartDate());
        merged.setEndDate(course.getEndDate() == null ? dbCourse.getEndDate() : course.getEndDate());
        merged.setStatus(StringUtils.defaultString(course.getStatus(), dbCourse.getStatus()));
        merged.setDescription(StringUtils.defaultString(course.getDescription(), dbCourse.getDescription()));
        merged.setRemark(StringUtils.defaultString(course.getRemark(), dbCourse.getRemark()));
        return merged;
    }

    private boolean isCourseScheduleConflict(EduCourse left, EduCourse right)
    {
        if (!"0".equals(StringUtils.defaultString(left.getStatus(), "0")) || !"0".equals(right.getStatus()))
        {
            return false;
        }
        if (!isDateRangeOverlap(left.getStartDate(), left.getEndDate(), right.getStartDate(), right.getEndDate()))
        {
            return false;
        }
        List<CourseSlot> leftSlots = parseCourseSlots(left.getWeekDay(), left.getStartTime(), left.getEndTime());
        List<CourseSlot> rightSlots = parseCourseSlots(right.getWeekDay(), right.getStartTime(), right.getEndTime());
        for (CourseSlot leftSlot : leftSlots)
        {
            for (CourseSlot rightSlot : rightSlots)
            {
                if (leftSlot.weekDay.equals(rightSlot.weekDay)
                        && isTimeOverlap(leftSlot.startTime, leftSlot.endTime, rightSlot.startTime, rightSlot.endTime))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDateRangeOverlap(Date startA, Date endA, Date startB, Date endB)
    {
        if (startA == null || endA == null || startB == null || endB == null)
        {
            return true;
        }
        return !startA.after(endB) && !startB.after(endA);
    }

    private boolean isTimeOverlap(String startA, String endA, String startB, String endB)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date parsedStartA = formatter.parse(startA);
            Date parsedEndA = formatter.parse(endA);
            Date parsedStartB = formatter.parse(startB);
            Date parsedEndB = formatter.parse(endB);
            return parsedStartA.before(parsedEndB) && parsedStartB.before(parsedEndA);
        }
        catch (ParseException e)
        {
            return true;
        }
    }

    private List<CourseSlot> parseCourseSlots(String weekDayText, String fallbackStartTime, String fallbackEndTime)
    {
        List<CourseSlot> slots = new ArrayList<>();
        Matcher matcher = COURSE_SLOT_PATTERN.matcher(StringUtils.defaultString(weekDayText));
        while (matcher.find())
        {
            String weekDay = "周天".equals(matcher.group(1)) ? "周日" : matcher.group(1);
            slots.add(new CourseSlot(weekDay,
                    StringUtils.defaultString(matcher.group(2), StringUtils.defaultString(fallbackStartTime, "16:00")),
                    StringUtils.defaultString(matcher.group(3), StringUtils.defaultString(fallbackEndTime, "17:30"))));
        }
        if (slots.isEmpty() && StringUtils.isNotEmpty(weekDayText))
        {
            String[] weekOptions = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
            for (String weekDay : weekOptions)
            {
                if (weekDayText.contains(weekDay))
                {
                    slots.add(new CourseSlot(weekDay,
                            StringUtils.defaultString(fallbackStartTime, "16:00"),
                            StringUtils.defaultString(fallbackEndTime, "17:30")));
                }
            }
        }
        if (slots.isEmpty())
        {
            slots.add(new CourseSlot("周一",
                    StringUtils.defaultString(fallbackStartTime, "16:00"),
                    StringUtils.defaultString(fallbackEndTime, "17:30")));
        }
        return slots;
    }

    private static class CourseSlot
    {
        private final String weekDay;

        private final String startTime;

        private final String endTime;

        CourseSlot(String weekDay, String startTime, String endTime)
        {
            this.weekDay = weekDay;
            this.startTime = startTime;
            this.endTime = endTime;
        }
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
