package com.eapple.common.utils;

/**
 * Log formatting helpers.
 *
 * @author EAPPLE
 */
public class LogUtils
{
    public static String getBlock(Object msg)
    {
        if (msg == null)
        {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }
}
