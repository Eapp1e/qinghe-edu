package com.ruoyi.system.service.edu;

import java.util.List;
import com.ruoyi.system.domain.edu.EduCourse;

public interface IEduCourseService
{
    EduCourse selectCourseById(Long courseId);

    List<EduCourse> selectCourseList(EduCourse course);

    List<EduCourse> selectMyCourseList();

    int insertCourse(EduCourse course);

    int updateCourse(EduCourse course);

    int deleteCourseByIds(Long[] courseIds);

    int enrollCourse(Long courseId, Long studentUserId);

    String generateCourseNotice(Long courseId);

    String generateTeachingSuggestion(Long courseId);
}
