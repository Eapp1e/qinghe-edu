package com.eapple.common.exception.user;

/**
 * 鐢ㄦ埛瀵嗙爜涓嶆纭垨涓嶇鍚堣鑼冨紓甯哥被
 * 
 * @author Eapp1e
 */
public class UserPasswordNotMatchException extends UserException
{
    private static final long serialVersionUID = 1L;

    public UserPasswordNotMatchException()
    {
        super("user.password.not.match", null);
    }
}
