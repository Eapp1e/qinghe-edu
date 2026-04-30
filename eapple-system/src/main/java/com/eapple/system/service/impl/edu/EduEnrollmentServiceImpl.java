package com.eapple.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.system.domain.edu.EduCourseEnrollment;
import com.eapple.system.mapper.edu.EduEnrollmentMapper;
import com.eapple.system.service.edu.IEduEnrollmentService;
import com.eapple.system.util.EduSchoolScopeUtils;

@Service
public class EduEnrollmentServiceImpl implements IEduEnrollmentService
{
    @Autowired
    private EduEnrollmentMapper enrollmentMapper;

    @Override
    public List<EduCourseEnrollment> selectEnrollmentList(EduCourseEnrollment enrollment)
    {
        if (SecurityUtils.hasExactRole("edu_teacher"))
        {
            enrollment.setTeacherUserId(SecurityUtils.getUserId());
        }
        if (SecurityUtils.hasExactRole("edu_parent"))
        {
            enrollment.setParentUserId(SecurityUtils.getUserId());
        }
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            enrollment.setStudentUserId(SecurityUtils.getUserId());
        }
        EduSchoolScopeUtils.applySchoolScope(enrollment);
        return enrollmentMapper.selectEnrollmentList(enrollment);
    }

    @Override
    public EduCourseEnrollment selectEnrollmentById(Long enrollmentId)
    {
        return enrollmentMapper.selectEnrollmentById(enrollmentId);
    }

    @Override
    public int updateEnrollment(EduCourseEnrollment enrollment)
    {
        EduCourseEnrollment dbEnrollment = enrollmentMapper.selectEnrollmentById(enrollment.getEnrollmentId());
        if (dbEnrollment == null)
        {
            throw new ServiceException("报名记录不存在");
        }
        if (SecurityUtils.hasExactRole("edu_parent") || SecurityUtils.hasExactRole("edu_admin")
                || (SecurityUtils.isAdmin() && !SecurityUtils.hasExactRole("edu_teacher")
                && !SecurityUtils.hasExactRole("edu_student")))
        {
            throw new ServiceException("家长端和管理端仅支持查看学习记录");
        }
        if (SecurityUtils.hasExactRole("edu_teacher"))
        {
            if (!SecurityUtils.getUserId().equals(dbEnrollment.getTeacherUserId()))
            {
                throw new ServiceException("只能维护本人课程的上课记录与反馈");
            }
            enrollment.setLearningRecord(null);
        }
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            if (!SecurityUtils.getUserId().equals(dbEnrollment.getStudentUserId()))
            {
                throw new ServiceException("学生只能填写自己的学习记录");
            }
            enrollment.setStatus(null);
            enrollment.setInteractionSummary(null);
            enrollment.setRemark(null);
        }
        enrollment.setUpdateBy(SecurityUtils.getUsername());
        return enrollmentMapper.updateEnrollment(enrollment);
    }
}
