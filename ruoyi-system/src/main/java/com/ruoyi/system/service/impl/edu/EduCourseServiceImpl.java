package com.ruoyi.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.edu.EduCourse;
import com.ruoyi.system.domain.edu.EduCourseEnrollment;
import com.ruoyi.system.domain.edu.EduStudentProfile;
import com.ruoyi.system.mapper.edu.EduCourseMapper;
import com.ruoyi.system.mapper.edu.EduEnrollmentMapper;
import com.ruoyi.system.mapper.edu.EduStudentProfileMapper;
import com.ruoyi.system.service.edu.IEduAiService;
import com.ruoyi.system.service.edu.IEduCourseService;

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
        if (!SecurityUtils.hasRole("edu_teacher") && !SecurityUtils.isAdmin())
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
        if (!SecurityUtils.isAdmin())
        {
            for (Long courseId : courseIds)
            {
                ensureCourseOwner(ensureCourseExists(courseId));
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
        enrollment.setSignupSource(SecurityUtils.hasRole("edu_parent") ? "parent" : "student");
        enrollment.setCreateBy(SecurityUtils.getUsername());

        int rows = enrollmentMapper.insertEnrollment(enrollment);
        enrollmentMapper.increaseCourseCapacity(courseId);
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
                + "\n时间：" + course.getWeekDay() + " " + course.getStartTime() + "-" + course.getEndTime()
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

    private void fillQueryScope(EduCourse course, boolean onlyMine)
    {
        if (SecurityUtils.hasRole("edu_teacher"))
        {
            course.setTeacherUserId(SecurityUtils.getUserId());
        }
        if (SecurityUtils.hasRole("edu_student"))
        {
            course.getParams().put("currentUserId", SecurityUtils.getUserId());
            course.getParams().put("currentRole", "student");
            course.getParams().put("onlyMine", onlyMine);
            if (!onlyMine)
            {
                course.setStatus("0");
            }
        }
        if (SecurityUtils.hasRole("edu_parent"))
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

    private void ensureCourseOwner(EduCourse course)
    {
        if (!SecurityUtils.isAdmin() && SecurityUtils.hasRole("edu_teacher")
                && !SecurityUtils.getUserId().equals(course.getTeacherUserId()))
        {
            throw new ServiceException("只能操作本人发布的课程");
        }
    }

    private Long resolveStudentUserId(Long studentUserId)
    {
        if (SecurityUtils.hasRole("edu_student"))
        {
            Long currentUserId = SecurityUtils.getUserId();
            if (studentUserId != null && !currentUserId.equals(studentUserId))
            {
                throw new ServiceException("学生只能为自己报名");
            }
            return currentUserId;
        }

        if (SecurityUtils.hasRole("edu_parent"))
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
}
