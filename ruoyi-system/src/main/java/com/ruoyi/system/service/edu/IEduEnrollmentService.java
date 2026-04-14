package com.ruoyi.system.service.edu;

import java.util.List;
import com.ruoyi.system.domain.edu.EduCourseEnrollment;

public interface IEduEnrollmentService
{
    List<EduCourseEnrollment> selectEnrollmentList(EduCourseEnrollment enrollment);

    EduCourseEnrollment selectEnrollmentById(Long enrollmentId);

    int updateEnrollment(EduCourseEnrollment enrollment);
}
