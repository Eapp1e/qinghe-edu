package com.qinghe.system.service.impl.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qinghe.common.utils.SecurityUtils;
import com.qinghe.system.domain.SysNotice;
import com.qinghe.system.domain.edu.EduAiLog;
import com.qinghe.system.domain.edu.EduCourse;
import com.qinghe.system.domain.edu.EduCourseEnrollment;
import com.qinghe.system.domain.edu.EduDashboardVo;
import com.qinghe.system.domain.edu.EduFamilyTask;
import com.qinghe.system.domain.edu.EduHomeworkQuestion;
import com.qinghe.system.domain.edu.EduStudentProfile;
import com.qinghe.system.mapper.SysNoticeMapper;
import com.qinghe.system.mapper.edu.EduAiLogMapper;
import com.qinghe.system.mapper.edu.EduCourseMapper;
import com.qinghe.system.mapper.edu.EduEnrollmentMapper;
import com.qinghe.system.mapper.edu.EduFamilyTaskMapper;
import com.qinghe.system.mapper.edu.EduHomeworkQuestionMapper;
import com.qinghe.system.mapper.edu.EduStudentProfileMapper;
import com.qinghe.system.service.edu.IEduDashboardService;
import com.qinghe.system.util.EduSchoolScopeUtils;

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

    @Autowired
    private SysNoticeMapper noticeMapper;

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
        SysNotice noticeQuery = new SysNotice();

        applySchoolScope(courseQuery, enrollmentQuery, questionQuery, logQuery, parentAdviceQuery,
                familyTaskQuery, completedFamilyTaskQuery, profileQuery);
        applySchoolScope(dynamicCourseQuery, new EduCourseEnrollment(), dynamicQuestionQuery, dynamicLogQuery,
                new EduAiLog(), dynamicFamilyTaskQuery, new EduFamilyTask(), new EduStudentProfile());
        EduSchoolScopeUtils.applySchoolScope(noticeQuery);
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
        noticeQuery.setStatus("0");

        vo.setTotalCourses(courseMapper.countCourses(courseQuery));
        vo.setTotalEnrollments(enrollmentMapper.countEnrollments(enrollmentQuery));
        vo.setTotalQuestions(questionMapper.countQuestions(questionQuery));
        vo.setTotalAiCalls(aiLogMapper.countAiLogs(logQuery));
        vo.setTotalParentAdvices(aiLogMapper.countAiLogs(parentAdviceQuery));
        vo.setTotalFamilyTasks(familyTaskMapper.countTasks(familyTaskQuery));
        vo.setCompletedFamilyTasks(familyTaskMapper.countTasks(completedFamilyTaskQuery));
        vo.setActiveStudents(profileMapper.countActiveStudents(profileQuery));
        vo.setActiveTeachers(courseMapper.countActiveTeachers(courseQuery));

        List<EduCourse> recentCourses = courseMapper.selectCourseList(courseQuery);
        List<EduCourse> popularCourses = courseMapper.selectPopularCourses(courseQuery);
        List<EduHomeworkQuestion> recentQuestions = questionMapper.selectQuestionList(dynamicQuestionQuery);
        List<EduAiLog> recentAiLogs = aiLogMapper.selectAiLogList(dynamicLogQuery);
        List<EduFamilyTask> recentFamilyTasks = familyTaskMapper.selectTaskList(dynamicFamilyTaskQuery);
        List<SysNotice> recentNotices = noticeMapper.selectNoticeList(noticeQuery);
        if (recentNotices.size() > 5)
        {
            recentNotices = recentNotices.subList(0, 5);
        }
        vo.setRecentCourses(recentCourses);
        vo.setPopularCourses(popularCourses);
        vo.setRecentQuestions(recentQuestions);
        vo.setRecentAiLogs(recentAiLogs);
        vo.setRecentFamilyTasks(recentFamilyTasks);
        vo.setRecentNotices(recentNotices);
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

    private void applySchoolScope(EduCourse courseQuery, EduCourseEnrollment enrollmentQuery,
            EduHomeworkQuestion questionQuery, EduAiLog logQuery, EduAiLog parentAdviceQuery,
            EduFamilyTask familyTaskQuery, EduFamilyTask completedFamilyTaskQuery, EduStudentProfile profileQuery)
    {
        EduSchoolScopeUtils.applySchoolScope(courseQuery);
        EduSchoolScopeUtils.applySchoolScope(enrollmentQuery);
        EduSchoolScopeUtils.applySchoolScope(questionQuery);
        EduSchoolScopeUtils.applySchoolScope(logQuery);
        EduSchoolScopeUtils.applySchoolScope(parentAdviceQuery);
        EduSchoolScopeUtils.applySchoolScope(familyTaskQuery);
        EduSchoolScopeUtils.applySchoolScope(completedFamilyTaskQuery);
        EduSchoolScopeUtils.applySchoolScope(profileQuery);
    }
}
