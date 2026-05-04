package com.qinghe.common.core.domain.model;

/**
 * 用户注册对象。
 *
 * @author Eapp1e
 */
public class RegisterBody extends LoginBody
{
    private String teacherType;

    private Long schoolId;

    private String schoolName;

    public String getTeacherType()
    {
        return teacherType;
    }

    public void setTeacherType(String teacherType)
    {
        this.teacherType = teacherType;
    }

    public Long getSchoolId()
    {
        return schoolId;
    }

    public void setSchoolId(Long schoolId)
    {
        this.schoolId = schoolId;
    }

    public String getSchoolName()
    {
        return schoolName;
    }

    public void setSchoolName(String schoolName)
    {
        this.schoolName = schoolName;
    }
}
