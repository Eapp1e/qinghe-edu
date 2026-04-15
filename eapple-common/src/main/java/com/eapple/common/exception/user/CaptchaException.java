package com.eapple.common.exception.user;

/**
 * 楠岃瘉鐮侀敊璇紓甯哥被
 * 
 * @author Eapp1e
 */
public class CaptchaException extends UserException
{
    private static final long serialVersionUID = 1L;

    public CaptchaException()
    {
        super("user.jcaptcha.error", null);
    }
}
