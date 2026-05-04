package com.qinghe.system.domain.edu;

import com.qinghe.common.annotation.Excel;
import com.qinghe.common.core.domain.BaseEntity;

public class EduHomeworkQuestion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long questionId;

    private Long courseId;

    @Excel(name = "课程名称")
    private String courseName;

    private Long studentUserId;

    @Excel(name = "学生")
    private String studentName;

    private Long parentUserId;

    private Long teacherUserId;

    @Excel(name = "问题标题")
    private String questionTitle;

    @Excel(name = "问题内容")
    private String questionContent;

    @Excel(name = "AI解答")
    private String aiAnswer;

    @Excel(name = "答疑状态")
    private String answerStatus;

    @Excel(name = "安全标记")
    private String safetyFlag;

    public Long getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Long questionId)
    {
        this.questionId = questionId;
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

    public Long getTeacherUserId()
    {
        return teacherUserId;
    }

    public void setTeacherUserId(Long teacherUserId)
    {
        this.teacherUserId = teacherUserId;
    }

    public String getQuestionTitle()
    {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle)
    {
        this.questionTitle = questionTitle;
    }

    public String getQuestionContent()
    {
        return questionContent;
    }

    public void setQuestionContent(String questionContent)
    {
        this.questionContent = questionContent;
    }

    public String getAiAnswer()
    {
        return aiAnswer;
    }

    public void setAiAnswer(String aiAnswer)
    {
        this.aiAnswer = aiAnswer;
    }

    public String getAnswerStatus()
    {
        return answerStatus;
    }

    public void setAnswerStatus(String answerStatus)
    {
        this.answerStatus = answerStatus;
    }

    public String getSafetyFlag()
    {
        return safetyFlag;
    }

    public void setSafetyFlag(String safetyFlag)
    {
        this.safetyFlag = safetyFlag;
    }
}