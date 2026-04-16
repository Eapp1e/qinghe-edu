package com.eapple.system.mapper.edu;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.eapple.system.domain.edu.EduCourseEnrollment;

public interface EduEnrollmentMapper
{
    EduCourseEnrollment selectEnrollmentById(Long enrollmentId);

    EduCourseEnrollment selectEnrollmentByCourseAndStudent(@Param("courseId") Long courseId, @Param("studentUserId") Long studentUserId);

    List<EduCourseEnrollment> selectEnrollmentList(EduCourseEnrollment enrollment);

    int insertEnrollment(EduCourseEnrollment enrollment);

    int updateEnrollment(EduCourseEnrollment enrollment);

    int increaseCourseCapacity(Long courseId);

    int decreaseCourseCapacity(Long courseId);

    int deleteEnrollmentById(Long enrollmentId);

    Long countEnrollments(EduCourseEnrollment enrollment);
}
