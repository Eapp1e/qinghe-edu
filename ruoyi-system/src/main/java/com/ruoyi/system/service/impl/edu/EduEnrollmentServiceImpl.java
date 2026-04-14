package com.ruoyi.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.edu.EduCourseEnrollment;
import com.ruoyi.system.mapper.edu.EduEnrollmentMapper;
import com.ruoyi.system.service.edu.IEduEnrollmentService;

@Service
public class EduEnrollmentServiceImpl implements IEduEnrollmentService
{
    @Autowired
    private EduEnrollmentMapper enrollmentMapper;

    @Override
    public List<EduCourseEnrollment> selectEnrollmentList(EduCourseEnrollment enrollment)
    {
        if (SecurityUtils.hasRole("edu_teacher"))
        {
            enrollment.setTeacherUserId(SecurityUtils.getUserId());
        }
        if (SecurityUtils.hasRole("edu_parent"))
        {
            enrollment.setParentUserId(SecurityUtils.getUserId());
        }
        if (SecurityUtils.hasRole("edu_student"))
        {
            enrollment.setStudentUserId(SecurityUtils.getUserId());
        }
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
        if (!SecurityUtils.isAdmin() && SecurityUtils.hasRole("edu_teacher")
                && !SecurityUtils.getUserId().equals(dbEnrollment.getTeacherUserId()))
        {
            throw new ServiceException("只能管理本人课程的报名记录");
        }
        enrollment.setUpdateBy(SecurityUtils.getUsername());
        return enrollmentMapper.updateEnrollment(enrollment);
    }
}
