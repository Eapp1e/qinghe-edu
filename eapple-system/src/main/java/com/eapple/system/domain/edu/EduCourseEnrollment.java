package com.eapple.system.domain.edu;

import com.eapple.common.annotation.Excel;
import com.eapple.common.core.domain.BaseEntity;

public class EduCourseEnrollment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long enrollmentId;

    private Long courseId;

    @Excel(name = "璇剧▼鍚嶇О")
    private String courseName;

    private Long studentUserId;

    @Excel(name = "瀛︾敓")
    private String studentName;

    private Long parentUserId;

    @Excel(name = "瀹堕暱")
    private String parentName;

    private Long teacherUserId;

    @Excel(name = "鏁欏笀")
    private String teacherName;

    @Excel(name = "鎶ュ悕鐘舵€?)
    private String status;

    @Excel(name = "鎶ュ悕鏉ユ簮")
    private String signupSource;

    @Excel(name = "瀛︿範璁板綍")
    private String learningRecord;

    private String interactionSummary;

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
}
