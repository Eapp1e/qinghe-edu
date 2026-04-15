package com.eapple.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.domain.edu.EduCourse;
import com.eapple.system.domain.edu.EduCourseEnrollment;
import com.eapple.system.domain.edu.EduStudentProfile;
import com.eapple.system.mapper.edu.EduCourseMapper;
import com.eapple.system.mapper.edu.EduEnrollmentMapper;
import com.eapple.system.mapper.edu.EduStudentProfileMapper;
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
            throw new ServiceException("鍙湁鏁欏笀鎴栫鐞嗗憳鍙互鍙戝竷璇剧▼");
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
            throw new ServiceException("褰撳墠璇剧▼鏈紑鏀炬姤鍚?);
        }
        Long targetStudentId = resolveStudentUserId(studentUserId);
        EduStudentProfile profile = profileMapper.selectProfileByStudentUserId(targetStudentId);
        if (profile == null)
        {
            throw new ServiceException("璇峰厛缁存姢瀛︾敓妗ｆ鍚庡啀鎶ュ悕");
        }
        if (course.getCurrentCapacity() != null && course.getMaxCapacity() != null
                && course.getCurrentCapacity() >= course.getMaxCapacity())
        {
            throw new ServiceException("璇剧▼鍚嶉宸叉弧");
        }
        if (enrollmentMapper.selectEnrollmentByCourseAndStudent(courseId, targetStudentId) != null)
        {
            throw new ServiceException("璇ュ鐢熷凡鎶ュ悕姝よ绋?);
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
        String prompt = "璇剧▼鍚嶇О锛? + course.getCourseName()
                + "\n鍒嗙被锛? + course.getCategory()
                + "\n鏁欏笀锛? + course.getTeacherName()
                + "\n鏃堕棿锛? + course.getWeekDay() + " " + course.getStartTime() + "-" + course.getEndTime()
                + "\n鍦扮偣锛? + course.getCampus()
                + "\n璇剧▼绠€浠嬶細" + StringUtils.defaultString(course.getDescription());
        String content = aiService.generateCourseNotice(courseId, prompt);
        courseMapper.updateAiNotice(courseId, content);
        return content;
    }

    @Override
    public String generateTeachingSuggestion(Long courseId)
    {
        EduCourse course = ensureCourseExists(courseId);
        ensureCourseOwner(course);
        String prompt = "璇剧▼鍚嶇О锛? + course.getCourseName()
                + "\n闈㈠悜瀵硅薄锛氫腑灏忓璇惧悗鏈嶅姟瀛︾敓"
                + "\n璇剧▼绠€浠嬶細" + StringUtils.defaultString(course.getDescription())
                + "\n褰撳墠鎶ュ悕浜烘暟锛? + String.valueOf(course.getCurrentCapacity());
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
            throw new ServiceException("璇剧▼涓嶅瓨鍦?);
        }
        return course;
    }

    private void ensureCourseOwner(EduCourse course)
    {
        if (!SecurityUtils.isAdmin() && SecurityUtils.hasRole("edu_teacher")
                && !SecurityUtils.getUserId().equals(course.getTeacherUserId()))
        {
            throw new ServiceException("鍙兘鎿嶄綔鏈汉鍙戝竷鐨勮绋?);
        }
    }

    private Long resolveStudentUserId(Long studentUserId)
    {
        if (SecurityUtils.hasRole("edu_student"))
        {
            Long currentUserId = SecurityUtils.getUserId();
            if (studentUserId != null && !currentUserId.equals(studentUserId))
            {
                throw new ServiceException("瀛︾敓鍙兘涓鸿嚜宸辨姤鍚?);
            }
            return currentUserId;
        }

        if (SecurityUtils.hasRole("edu_parent"))
        {
            if (studentUserId == null)
            {
                throw new ServiceException("瀹堕暱鎶ュ悕鏃跺繀椤婚€夋嫨瀛╁瓙");
            }
            EduStudentProfile profile = profileMapper.selectProfileByStudentUserId(studentUserId);
            if (profile == null || !SecurityUtils.getUserId().equals(profile.getParentUserId()))
            {
                throw new ServiceException("鍙兘涓哄凡鍏宠仈鐨勫瀛愭姤鍚?);
            }
            return studentUserId;
        }
        return studentUserId;
    }
}
