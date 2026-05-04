package com.qinghe.system.domain.edu;

import java.util.List;
import com.qinghe.common.annotation.Excel;
import com.qinghe.common.core.domain.BaseEntity;

public class EduFamilyTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long taskId;

    private Long parentUserId;

    @Excel(name = "家长")
    private String parentName;

    private Long studentUserId;

    @Excel(name = "学生")
    private String studentName;

    @Excel(name = "任务标题")
    private String taskTitle;

    private String taskType;

    private String taskContent;

    private Integer rewardPoints;

    private String rewardText;

    private String dueDate;

    private String proofImages;

    private String studentFeedback;

    private String parentComment;

    private String status;

    private List<Long> boundStudentUserIds;

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
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

    public String getTaskTitle()
    {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle)
    {
        this.taskTitle = taskTitle;
    }

    public String getTaskType()
    {
        return taskType;
    }

    public void setTaskType(String taskType)
    {
        this.taskType = taskType;
    }

    public String getTaskContent()
    {
        return taskContent;
    }

    public void setTaskContent(String taskContent)
    {
        this.taskContent = taskContent;
    }

    public Integer getRewardPoints()
    {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints)
    {
        this.rewardPoints = rewardPoints;
    }

    public String getRewardText()
    {
        return rewardText;
    }

    public void setRewardText(String rewardText)
    {
        this.rewardText = rewardText;
    }

    public String getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(String dueDate)
    {
        this.dueDate = dueDate;
    }

    public String getProofImages()
    {
        return proofImages;
    }

    public void setProofImages(String proofImages)
    {
        this.proofImages = proofImages;
    }

    public String getStudentFeedback()
    {
        return studentFeedback;
    }

    public void setStudentFeedback(String studentFeedback)
    {
        this.studentFeedback = studentFeedback;
    }

    public String getParentComment()
    {
        return parentComment;
    }

    public void setParentComment(String parentComment)
    {
        this.parentComment = parentComment;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public List<Long> getBoundStudentUserIds()
    {
        return boundStudentUserIds;
    }

    public void setBoundStudentUserIds(List<Long> boundStudentUserIds)
    {
        this.boundStudentUserIds = boundStudentUserIds;
    }
}
