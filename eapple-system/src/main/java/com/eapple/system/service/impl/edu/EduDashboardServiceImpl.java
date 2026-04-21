package com.eapple.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.system.domain.edu.EduAiLog;
import com.eapple.system.domain.edu.EduCourse;
import com.eapple.system.domain.edu.EduCourseEnrollment;
import com.eapple.system.domain.edu.EduDashboardVo;
import com.eapple.system.domain.edu.EduHomeworkQuestion;
import com.eapple.system.domain.edu.EduStudentProfile;
import com.eapple.system.mapper.edu.EduAiLogMapper;
import com.eapple.system.mapper.edu.EduCourseMapper;
import com.eapple.system.mapper.edu.EduEnrollmentMapper;
import com.eapple.system.mapper.edu.EduHomeworkQuestionMapper;
import com.eapple.system.mapper.edu.EduStudentProfileMapper;
import com.eapple.system.service.edu.IEduDashboardService;

@Service
public class EduDashboardServiceImpl implements IEduDashboardService
{
    @Autowired
    private EduCourseMapper courseMapper;

    @Autowired
    private EduEnrollmentMapper enrollmentMapper;

    @Autowired
    private EduHomeworkQuestionMapper questionMapper;

    @Autowired
    private EduAiLogMapper aiLogMapper;

    @Autowired
    private EduStudentProfileMapper profileMapper;

    @Override
    public EduDashboardVo getDashboard()
    {
        EduDashboardVo vo = new EduDashboardVo();

        EduCourse courseQuery = new EduCourse();
        EduCourseEnrollment enrollmentQuery = new EduCourseEnrollment();
        EduHomeworkQuestion questionQuery = new EduHomeworkQuestion();
        EduAiLog logQuery = new EduAiLog();
        EduStudentProfile profileQuery = new EduStudentProfile();

        courseQuery.getParams().put("limitTop", 5);
        questionQuery.getParams().put("limitTop", 5);
        logQuery.getParams().put("limitTop", 5);

        vo.setTotalCourses(courseMapper.countCourses(courseQuery));
        vo.setTotalEnrollments(enrollmentMapper.countEnrollments(enrollmentQuery));
        vo.setTotalQuestions(questionMapper.countQuestions(questionQuery));
        vo.setTotalAiCalls(aiLogMapper.countAiLogs(logQuery));
        vo.setActiveStudents(profileMapper.countActiveStudents(profileQuery));
        vo.setActiveTeachers(courseMapper.countActiveTeachers());

        List<EduCourse> recentCourses = courseMapper.selectCourseList(courseQuery);
        List<EduCourse> popularCourses = courseMapper.selectPopularCourses(courseQuery);
        List<EduHomeworkQuestion> recentQuestions = questionMapper.selectQuestionList(questionQuery);
        List<EduAiLog> recentAiLogs = aiLogMapper.selectAiLogList(logQuery);
        vo.setRecentCourses(recentCourses);
        vo.setPopularCourses(popularCourses);
        vo.setRecentQuestions(recentQuestions);
        vo.setRecentAiLogs(recentAiLogs);
        return vo;
    }
}
