package com.eapple.framework.security.context;

import org.springframework.security.core.Authentication;

/**
 * 韬唤楠岃瘉淇℃伅
 * 
 * @author Eapp1e
 */
public class AuthenticationContextHolder
{
    private static final ThreadLocal<Authentication> contextHolder = new ThreadLocal<>();

    public static Authentication getContext()
    {
        return contextHolder.get();
    }

    public static void setContext(Authentication context)
    {
        contextHolder.set(context);
    }

    public static void clearContext()
    {
        contextHolder.remove();
    }
}
