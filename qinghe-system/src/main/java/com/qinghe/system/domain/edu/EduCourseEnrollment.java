package com.qinghe.system.domain.edu;

import com.qinghe.common.annotation.Excel;
import com.qinghe.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class EduCourseEnrollment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long enrollmentId;

    private Long courseId;

    @Excel(name = "课程名称")
    private String courseName;

    private Long studentUserId;

    @Excel(name = "学生")
    private String studentName;

    private Long parentUserId;

    @Excel(name = "家长")
    private String parentName;

    private Long teacherUserId;

    @Excel(name = "教师")
    private String teacherName;

    @Excel(name = "报名状态")
    private String status;

    @Excel(name = "报名来源")
    private String courseStatus;

    private String signupSource;

    @Excel(name = "学习记录")
    private String learningRecord;

    private String interactionSummary;

    private String campus;

    private String weekDay;

    private String startTime;

    private String endTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    public Long getEnrollmentId()
    {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId)
    {
        this.enrollmentId = enrollmentId;
    }

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    public String getCourseName()
    {
        return courseName;
    }

    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }

    public Long getStudentUserId()
    {
        return studentUserId;
    }

    public void setStudentUserId(Long studentUserId)
    {
        this.studentUserId = studentUserId;
    }

    public String getStudentName()
    {
        return studentName;
    }

    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }

    public Long getParentUserId()
    {
        return parentUserId;
    }

    public void setParentUserId(Long parentUserId)
    {
        this.parentUserId = parentUserId;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    public Long getTeacherUserId()
    {
        return teacherUserId;
    }

    public void setTeacherUserId(Long teacherUserId)
    {
        this.teacherUserId = teacherUserId;
    }

    public String getTeacherName()
    {
        return teacherName;
    }

    public void setTeacherName(String teacherName)
    {
        this.teacherName = teacherName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getCourseStatus()
    {
        return courseStatus;
    }

    public void setCourseStatus(String courseStatus)
    {
        this.courseStatus = courseStatus;
    }

    public String getSignupSource()
    {
        return signupSource;
    }

    public void setSignupSource(String signupSource)
    {
        this.signupSource = signupSource;
    }

    public String getLearningRecord()
    {
        return learningRecord;
    }

    public void setLearningRecord(String learningRecord)
    {
        this.learningRecord = learningRecord;
    }

    public String getInteractionSummary()
    {
        return interactionSummary;
    }

    public void setInteractionSummary(String interactionSummary)
    {
        this.interactionSummary = interactionSummary;
    }

    public String getCampus()
    {
        return campus;
    }

    public void setCampus(String campus)
    {
        this.campus = campus;
    }

    public String getWeekDay()
    {
        return weekDay;
    }

    public void setWeekDay(String weekDay)
    {
        this.weekDay = weekDay;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }
}
