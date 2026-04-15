package com.eapple.system.service.edu;

import java.util.List;
import com.eapple.system.domain.edu.EduCourseEnrollment;

public interface IEduEnrollmentService
{
    List<EduCourseEnrollment> selectEnrollmentList(EduCourseEnrollment enrollment);

    EduCourseEnrollment selectEnrollmentById(Long enrollmentId);

    int updateEnrollment(EduCourseEnrollment enrollment);
}
