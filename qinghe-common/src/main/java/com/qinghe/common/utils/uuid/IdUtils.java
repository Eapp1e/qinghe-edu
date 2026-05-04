package com.qinghe.common.utils.uuid;

/**
 * ID 生成工具类。
 *
 * @author Eapp1e
 */
public class IdUtils
{
    /**
     * 生成标准 UUID。
     *
     * @return 标准 UUID
     */
    public static String randomUUID()
    {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成不带短横线的 UUID。
     *
     * @return 精简 UUID
     */
    public static String simpleUUID()
    {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 生成快速 UUID。
     *
     * @return 快速 UUID
     */
    public static String fastUUID()
    {
        return UUID.fastUUID().toString();
    }

    /**
     * 生成不带短横线的快速 UUID。
     *
     * @return 精简快速 UUID
     */
    public static String fastSimpleUUID()
    {
        return UUID.fastUUID().toString(true);
    }
}
