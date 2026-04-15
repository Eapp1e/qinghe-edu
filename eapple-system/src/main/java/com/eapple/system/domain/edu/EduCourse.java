package com.eapple.system.domain.edu;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.eapple.common.annotation.Excel;
import com.eapple.common.core.domain.BaseEntity;

public class EduCourse extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long courseId;

    @Excel(name = "璇剧▼鍚嶇О")
    private String courseName;

    @Excel(name = "璇剧▼鍒嗙被")
    private String category;

    private Long teacherUserId;

    @Excel(name = "鏁欏笀")
    private String teacherName;

    @Excel(name = "鏍″尯")
    private String campus;

    @Excel(name = "涓婅鏄熸湡")
    private String weekDay;

    @Excel(name = "寮€濮嬫椂闂?)
    private String startTime;

    @Excel(name = "缁撴潫鏃堕棿")
    private String endTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @Excel(name = "鏈€澶у閲?)
    private Integer maxCapacity;

    private Integer currentCapacity;

    @Excel(name = "鐘舵€?)
    private String status;

    private String description;

    private String aiNotice;

    private String aiSuggestion;

    private Long enrollCount;

    private String enrolled;

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

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
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
}
