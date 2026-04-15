package com.eapple.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.common.exception.ServiceException;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.system.domain.edu.EduCourseEnrollment;
import com.eapple.system.mapper.edu.EduEnrollmentMapper;
import com.eapple.system.service.edu.IEduEnrollmentService;

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
            throw new ServiceException("鎶ュ悕璁板綍涓嶅瓨鍦?);
        }
        if (!SecurityUtils.isAdmin() && SecurityUtils.hasRole("edu_teacher")
                && !SecurityUtils.getUserId().equals(dbEnrollment.getTeacherUserId()))
        {
            throw new ServiceException("鍙兘绠＄悊鏈汉璇剧▼鐨勬姤鍚嶈褰?);
        }
        enrollment.setUpdateBy(SecurityUtils.getUsername());
        return enrollmentMapper.updateEnrollment(enrollment);
    }
}
