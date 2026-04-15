package com.eapple.system.domain.edu;

import com.eapple.common.annotation.Excel;
import com.eapple.common.core.domain.BaseEntity;

public class EduStudentProfile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long profileId;

    private Long studentUserId;

    @Excel(name = "瀛︾敓濮撳悕")
    private String studentName;

    private Long parentUserId;

    @Excel(name = "瀹堕暱濮撳悕")
    private String parentName;

    @Excel(name = "骞寸骇")
    private String gradeName;

    @Excel(name = "鐝骇")
    private String className;

    @Excel(name = "鎬у埆")
    private String gender;

    @Excel(name = "鍏磋叮鏍囩")
    private String interestTags;

    @Excel(name = "鐘舵€?)
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
