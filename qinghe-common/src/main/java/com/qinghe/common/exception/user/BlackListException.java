package com.qinghe.common.exception.user;

/**
 * 榛戝悕鍗旾P寮傚父绫?
 * 
 * @author Eapp1e
 */
public class BlackListException extends UserException
{
    private static final long serialVersionUID = 1L;

    public BlackListException()
    {
        super("login.blocked", null);
    }
}
