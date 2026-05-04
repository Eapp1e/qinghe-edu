package com.qinghe.system.domain.edu;

import com.qinghe.common.annotation.Excel;
import com.qinghe.common.core.domain.BaseEntity;

public class EduStudentProfile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long profileId;

    private Long studentUserId;

    @Excel(name = "学生姓名")
    private String studentName;

    private Long parentUserId;

    @Excel(name = "家长姓名")
    private String parentName;

    private String parentAccount;

    @Excel(name = "年级")
    private String gradeName;

    @Excel(name = "班级")
    private String className;

    @Excel(name = "性别")
    private String gender;

    @Excel(name = "兴趣标签")
    private String interestTags;

    @Excel(name = "状态")
    private String status;

    public Long getProfileId()
    {
        return profileId;
    }

    public void setProfileId(Long profileId)
    {
        this.profileId = profileId;
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

    public String getParentAccount()
    {
        return parentAccount;
    }

    public void setParentAccount(String parentAccount)
    {
        this.parentAccount = parentAccount;
    }

    public String getGradeName()
    {
        return gradeName;
    }

    public void setGradeName(String gradeName)
    {
        this.gradeName = gradeName;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getInterestTags()
    {
        return interestTags;
    }

    public void setInterestTags(String interestTags)
    {
        this.interestTags = interestTags;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
