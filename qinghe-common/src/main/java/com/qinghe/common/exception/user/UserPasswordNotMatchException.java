package com.qinghe.common.exception.user;

/**
 * 用户密码不匹配异常类。
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
