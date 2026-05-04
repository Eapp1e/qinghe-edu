package com.qinghe.system.domain.edu;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qinghe.common.annotation.Excel;
import com.qinghe.common.core.domain.BaseEntity;

public class EduCourse extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long courseId;

    @Excel(name = "课程编号")
    private String courseCode;

    @Excel(name = "课程名称")
    private String courseName;

    @Excel(name = "课程分类")
    private String category;

    @Excel(name = "开课范围")
    private String gradeScope;

    private Long teacherUserId;

    @Excel(name = "教师")
    private String teacherName;

    @Excel(name = "校区")
    private String campus;

    @Excel(name = "上课星期")
    private String weekDay;

    @Excel(name = "开始时间")
    private String startTime;

    @Excel(name = "结束时间")
    private String endTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @Excel(name = "最大容量")
    private Integer maxCapacity;

    private Integer currentCapacity;

    @Excel(name = "状态")
    private String status;

    private String description;

    private String aiNotice;

    private String aiSuggestion;

    private Long enrollCount;

    private String enrolled;

    private String runtimeStatus;

    private Long studentUserId;

    private Integer recommendationScore;

    private String recommendationReason;

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    public String getCourseCode()
    {
        return courseCode;
    }

    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }

    public String getCourseName()
    {
        return courseName;
    }

    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getGradeScope()
    {
        return gradeScope;
    }

    public void setGradeScope(String gradeScope)
    {
        this.gradeScope = gradeScope;
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

    public Integer getMaxCapacity()
    {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity)
    {
        this.maxCapacity = maxCapacity;
    }

    public Integer getCurrentCapacity()
    {
        return currentCapacity;
    }

    public void setCurrentCapacity(Integer currentCapacity)
    {
        this.currentCapacity = currentCapacity;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAiNotice()
    {
        return aiNotice;
    }

    public void setAiNotice(String aiNotice)
    {
        this.aiNotice = aiNotice;
    }

    public String getAiSuggestion()
    {
        return aiSuggestion;
    }

    public void setAiSuggestion(String aiSuggestion)
    {
        this.aiSuggestion = aiSuggestion;
    }

    public Long getEnrollCount()
    {
        return enrollCount;
    }

    public void setEnrollCount(Long enrollCount)
    {
        this.enrollCount = enrollCount;
    }

    public String getEnrolled()
    {
        return enrolled;
    }

    public void setEnrolled(String enrolled)
    {
        this.enrolled = enrolled;
    }

    public String getRuntimeStatus()
    {
        return runtimeStatus;
    }

    public void setRuntimeStatus(String runtimeStatus)
    {
        this.runtimeStatus = runtimeStatus;
    }

    public Long getStudentUserId()
    {
        return studentUserId;
    }

    public void setStudentUserId(Long studentUserId)
    {
        this.studentUserId = studentUserId;
    }

    public Integer getRecommendationScore()
    {
        return recommendationScore;
    }

    public void setRecommendationScore(Integer recommendationScore)
    {
        this.recommendationScore = recommendationScore;
    }

    public String getRecommendationReason()
    {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason)
    {
        this.recommendationReason = recommendationReason;
    }
}
