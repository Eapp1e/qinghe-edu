package com.eapple.system.domain.edu;

import java.util.List;

public class EduDashboardVo
{
    private Long totalCourses;
    private Long totalEnrollments;
    private Long totalQuestions;
    private Long totalAiCalls;
    private Long activeStudents;
    private Long activeTeachers;
    private List<EduCourse> recentCourses;
    private List<EduCourse> popularCourses;
    private List<EduHomeworkQuestion> recentQuestions;
    private List<EduAiLog> recentAiLogs;

    public Long getTotalCourses()
    {
        return totalCourses;
    }

    public void setTotalCourses(Long totalCourses)
    {
        this.totalCourses = totalCourses;
    }

    public Long getTotalEnrollments()
    {
        return totalEnrollments;
    }

    public void setTotalEnrollments(Long totalEnrollments)
    {
        this.totalEnrollments = totalEnrollments;
    }

    public Long getTotalQuestions()
    {
        return totalQuestions;
    }

    public void setTotalQuestions(Long totalQuestions)
    {
        this.totalQuestions = totalQuestions;
    }

    public Long getTotalAiCalls()
    {
        return totalAiCalls;
    }

    public void setTotalAiCalls(Long totalAiCalls)
    {
        this.totalAiCalls = totalAiCalls;
    }

    public Long getActiveStudents()
    {
        return activeStudents;
    }

    public void setActiveStudents(Long activeStudents)
    {
        this.activeStudents = activeStudents;
    }

    public Long getActiveTeachers()
    {
        return activeTeachers;
    }

    public void setActiveTeachers(Long activeTeachers)
    {
        this.activeTeachers = activeTeachers;
    }

    public List<EduCourse> getRecentCourses()
    {
        return recentCourses;
    }

    public void setRecentCourses(List<EduCourse> recentCourses)
    {
        this.recentCourses = recentCourses;
    }

    public List<EduCourse> getPopularCourses()
    {
        return popularCourses;
    }

    public void setPopularCourses(List<EduCourse> popularCourses)
    {
        this.popularCourses = popularCourses;
    }

    public List<EduHomeworkQuestion> getRecentQuestions()
    {
        return recentQuestions;
    }

    public void setRecentQuestions(List<EduHomeworkQuestion> recentQuestions)
    {
        this.recentQuestions = recentQuestions;
    }

    public List<EduAiLog> getRecentAiLogs()
    {
        return recentAiLogs;
    }

    public void setRecentAiLogs(List<EduAiLog> recentAiLogs)
    {
        this.recentAiLogs = recentAiLogs;
    }
}
