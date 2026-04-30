package com.eapple.system.mapper.edu;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.eapple.system.domain.edu.EduCourse;

public interface EduCourseMapper
{
    EduCourse selectCourseById(Long courseId);

    List<EduCourse> selectCourseList(EduCourse course);

    List<EduCourse> selectPopularCourses(EduCourse course);

    int insertCourse(EduCourse course);

    int updateCourse(EduCourse course);

    int deleteCourseByIds(Long[] courseIds);

    int updateAiNotice(@Param("courseId") Long courseId, @Param("aiNotice") String aiNotice);

    int updateAiSuggestion(@Param("courseId") Long courseId, @Param("aiSuggestion") String aiSuggestion);

    Long countCourses(EduCourse course);

    Long countActiveTeachers(EduCourse course);
}
