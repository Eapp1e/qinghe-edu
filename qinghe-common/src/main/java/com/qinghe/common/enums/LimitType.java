package com.qinghe.common.enums;

/**
 * 限流类型。
 *
 * @author Eapp1e
 */
public enum LimitType
{
    /**
     * 默认策略，按全局维度限流。
     */
    DEFAULT,

    /**
     * 按请求 IP 维度限流。
     */
    IP
}
