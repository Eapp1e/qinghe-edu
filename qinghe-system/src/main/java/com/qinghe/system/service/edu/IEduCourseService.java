package com.qinghe.system.service.edu;

import java.util.List;
import com.qinghe.system.domain.edu.EduCourse;

public interface IEduCourseService
{
    EduCourse selectCourseById(Long courseId);

    List<EduCourse> selectCourseList(EduCourse course);

    List<EduCourse> selectMyCourseList();

    int insertCourse(EduCourse course);

    int updateCourse(EduCourse course);

    int deleteCourseByIds(Long[] courseIds);

    int enrollCourse(Long courseId, Long studentUserId);

    int cancelEnrollment(Long courseId, Long studentUserId);

    String generateCourseNotice(Long courseId);

    String generateTeachingSuggestion(Long courseId);

    List<EduCourse> recommendCoursesByStudent(Long studentUserId);
}
