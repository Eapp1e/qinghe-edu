package com.eapple.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.system.domain.edu.EduAiLog;
import com.eapple.system.domain.edu.EduCourse;
import com.eapple.system.domain.edu.EduCourseEnrollment;
import com.eapple.system.domain.edu.EduDashboardVo;
import com.eapple.system.domain.edu.EduFamilyTask;
import com.eapple.system.domain.edu.EduHomeworkQuestion;
import com.eapple.system.domain.edu.EduStudentProfile;
import com.eapple.system.mapper.edu.EduAiLogMapper;
import com.eapple.system.mapper.edu.EduCourseMapper;
import com.eapple.system.mapper.edu.EduEnrollmentMapper;
import com.eapple.system.mapper.edu.EduFamilyTaskMapper;
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

    @Autowired
    private EduFamilyTaskMapper familyTaskMapper;

    @Override
    public EduDashboardVo getDashboard()
    {
        EduDashboardVo vo = new EduDashboardVo();

        EduCourse courseQuery = new EduCourse();
        EduCourseEnrollment enrollmentQuery = new EduCourseEnrollment();
        EduHomeworkQuestion questionQuery = new EduHomeworkQuestion();
        EduAiLog logQuery = new EduAiLog();
        EduAiLog parentAdviceQuery = new EduAiLog();
        EduFamilyTask familyTaskQuery = new EduFamilyTask();
        EduFamilyTask completedFamilyTaskQuery = new EduFamilyTask();
        EduStudentProfile profileQuery = new EduStudentProfile();
        EduCourse dynamicCourseQuery = new EduCourse();
        EduHomeworkQuestion dynamicQuestionQuery = new EduHomeworkQuestion();
        EduAiLog dynamicLogQuery = new EduAiLog();
        EduFamilyTask dynamicFamilyTaskQuery = new EduFamilyTask();

        applyDashboardScope(dynamicCourseQuery, new EduCourseEnrollment(), dynamicQuestionQuery, dynamicLogQuery,
                new EduAiLog(), dynamicFamilyTaskQuery, new EduFamilyTask(), new EduStudentProfile());

        courseQuery.getParams().put("limitTop", 100);
        dynamicCourseQuery.getParams().put("limitTop", 5);
        dynamicQuestionQuery.getParams().put("limitTop", 5);
        dynamicLogQuery.getParams().put("limitTop", 5);
        dynamicFamilyTaskQuery.getParams().put("limitTop", 5);
        parentAdviceQuery.setBusinessType("parent_diagnosis");
        parentAdviceQuery.setStatus("success");
        completedFamilyTaskQuery.setStatus("2");

        vo.setTotalCourses(courseMapper.countCourses(courseQuery));
        vo.setTotalEnrollments(enrollmentMapper.countEnrollments(enrollmentQuery));
        vo.setTotalQuestions(questionMapper.countQuestions(questionQuery));
        vo.setTotalAiCalls(aiLogMapper.countAiLogs(logQuery));
        vo.setTotalParentAdvices(aiLogMapper.countAiLogs(parentAdviceQuery));
        vo.setTotalFamilyTasks(familyTaskMapper.countTasks(new EduFamilyTask()));
        vo.setCompletedFamilyTasks(familyTaskMapper.countTasks(completedFamilyTaskQuery));
        vo.setActiveStudents(profileMapper.countActiveStudents(profileQuery));
        vo.setActiveTeachers(courseMapper.countActiveTeachers());

        List<EduCourse> recentCourses = courseMapper.selectCourseList(courseQuery);
        List<EduCourse> popularCourses = courseMapper.selectPopularCourses(courseQuery);
        List<EduHomeworkQuestion> recentQuestions = questionMapper.selectQuestionList(dynamicQuestionQuery);
        List<EduAiLog> recentAiLogs = aiLogMapper.selectAiLogList(dynamicLogQuery);
        List<EduFamilyTask> recentFamilyTasks = familyTaskMapper.selectTaskList(dynamicFamilyTaskQuery);
        vo.setRecentCourses(recentCourses);
        vo.setPopularCourses(popularCourses);
        vo.setRecentQuestions(recentQuestions);
        vo.setRecentAiLogs(recentAiLogs);
        vo.setRecentFamilyTasks(recentFamilyTasks);
        return vo;
    }

    private void applyDashboardScope(EduCourse courseQuery, EduCourseEnrollment enrollmentQuery,
            EduHomeworkQuestion questionQuery, EduAiLog logQuery, EduAiLog parentAdviceQuery,
            EduFamilyTask familyTaskQuery, EduFamilyTask completedFamilyTaskQuery, EduStudentProfile profileQuery)
    {
        if (SecurityUtils.isAdmin())
        {
            return;
        }
        Long userId = SecurityUtils.getUserId();
        if (SecurityUtils.hasExactRole("edu_teacher"))
        {
            courseQuery.setTeacherUserId(userId);
            enrollmentQuery.setTeacherUserId(userId);
            questionQuery.setTeacherUserId(userId);
            logQuery.setUserId(userId);
            parentAdviceQuery.setUserId(-1L);
            familyTaskQuery.setParentUserId(-1L);
            completedFamilyTaskQuery.setParentUserId(-1L);
            profileQuery.setStudentUserId(-1L);
            return;
        }
        if (SecurityUtils.hasExactRole("edu_parent"))
        {
            enrollmentQuery.setParentUserId(userId);
            questionQuery.setParentUserId(userId);
            logQuery.setUserId(userId);
            parentAdviceQuery.setUserId(userId);
            familyTaskQuery.setParentUserId(userId);
            completedFamilyTaskQuery.setParentUserId(userId);
            profileQuery.setParentUserId(userId);
            courseQuery.getParams().put("currentUserId", userId);
            courseQuery.getParams().put("currentRole", "parent");
            courseQuery.getParams().put("onlyMine", true);
            return;
        }
        if (SecurityUtils.hasExactRole("edu_student"))
        {
            enrollmentQuery.setStudentUserId(userId);
            questionQuery.setStudentUserId(userId);
            logQuery.setUserId(userId);
            parentAdviceQuery.setBizId(userId);
            familyTaskQuery.setStudentUserId(userId);
            completedFamilyTaskQuery.setStudentUserId(userId);
            profileQuery.setStudentUserId(userId);
            courseQuery.getParams().put("currentUserId", userId);
            courseQuery.getParams().put("currentRole", "student");
            courseQuery.getParams().put("onlyMine", true);
        }
    }
}
