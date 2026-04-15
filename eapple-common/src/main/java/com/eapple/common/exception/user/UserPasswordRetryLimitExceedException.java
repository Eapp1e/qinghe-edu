package com.eapple.common.exception.user;

/**
 * 鐢ㄦ埛閿欒鏈€澶ф鏁板紓甯哥被
 * 
 * @author Eapp1e
 */
public class UserPasswordRetryLimitExceedException extends UserException
{
    private static final long serialVersionUID = 1L;

    public UserPasswordRetryLimitExceedException(int retryLimitCount, int lockTime)
    {
        super("user.password.retry.limit.exceed", new Object[] { retryLimitCount, lockTime });
    }
}
