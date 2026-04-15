package com.eapple.common.enums;

/**
 * 鐢ㄦ埛鐘舵€?
 * 
 * @author Eapp1e
 */
public enum UserStatus
{
    OK("0", "姝ｅ父"), DISABLE("1", "鍋滅敤"), DELETED("2", "鍒犻櫎");

    private final String code;
    private final String info;

    UserStatus(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}
