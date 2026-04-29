package com.eapple.common.core.domain.model;

/**
 * 用户注册对象。
 *
 * @author Eapp1e
 */
public class RegisterBody extends LoginBody
{
    private String teacherType;

    public String getTeacherType()
    {
        return teacherType;
    }

    public void setTeacherType(String teacherType)
    {
        this.teacherType = teacherType;
    }
}
