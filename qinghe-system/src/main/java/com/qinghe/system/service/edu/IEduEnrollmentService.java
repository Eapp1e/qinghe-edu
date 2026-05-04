package com.qinghe.system.service.edu;

import java.util.List;
import com.qinghe.system.domain.edu.EduCourseEnrollment;

public interface IEduEnrollmentService
{
    List<EduCourseEnrollment> selectEnrollmentList(EduCourseEnrollment enrollment);

    EduCourseEnrollment selectEnrollmentById(Long enrollmentId);

    int updateEnrollment(EduCourseEnrollment enrollment);
}
