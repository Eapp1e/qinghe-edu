package com.ruoyi.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.edu.EduAiLog;
import com.ruoyi.system.domain.edu.EduCourse;
import com.ruoyi.system.domain.edu.EduCourseEnrollment;
import com.ruoyi.system.domain.edu.EduDashboardVo;
import com.ruoyi.system.domain.edu.EduHomeworkQuestion;
import com.ruoyi.system.domain.edu.EduStudentProfile;
import com.ruoyi.system.mapper.edu.EduAiLogMapper;
import com.ruoyi.system.mapper.edu.EduCourseMapper;
import com.ruoyi.system.mapper.edu.EduEnrollmentMapper;
import com.ruoyi.system.mapper.edu.EduHomeworkQuestionMapper;
import com.ruoyi.system.mapper.edu.EduStudentProfileMapper;
import com.ruoyi.system.service.edu.IEduDashboardService;

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

        if (SecurityUtils.hasRole("edu_teacher"))
        {
            courseQuery.setTeacherUserId(SecurityUtils.getUserId());
            enrollmentQuery.setTeacherUserId(SecurityUtils.getUserId());
            questionQuery.setTeacherUserId(SecurityUtils.getUserId());
        }
        else if (SecurityUtils.hasRole("edu_parent"))
        {
            courseQuery.getParams().put("currentUserId", SecurityUtils.getUserId());
            courseQuery.getParams().put("currentRole", "parent");
            courseQuery.getParams().put("onlyMine", true);
            enrollmentQuery.setParentUserId(SecurityUtils.getUserId());
            questionQuery.setParentUserId(SecurityUtils.getUserId());
            logQuery.setUserId(SecurityUtils.getUserId());
            profileQuery.setParentUserId(SecurityUtils.getUserId());
        }
        else if (SecurityUtils.hasRole("edu_student"))
        {
            courseQuery.getParams().put("currentUserId", SecurityUtils.getUserId());
            courseQuery.getParams().put("currentRole", "student");
            courseQuery.getParams().put("onlyMine", true);
            enrollmentQuery.setStudentUserId(SecurityUtils.getUserId());
            questionQuery.setStudentUserId(SecurityUtils.getUserId());
            logQuery.setUserId(SecurityUtils.getUserId());
            profileQuery.setStudentUserId(SecurityUtils.getUserId());
        }

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
        List<EduHomeworkQuestion> recentQuestions = questionMapper.selectQuestionList(questionQuery);
        List<EduAiLog> recentAiLogs = aiLogMapper.selectAiLogList(logQuery);
        vo.setRecentCourses(recentCourses);
        vo.setRecentQuestions(recentQuestions);
        vo.setRecentAiLogs(recentAiLogs);
        return vo;
    }
}
