package com.qinghe.common.exception;

/**
 * 全局异常。
 *
 * @author Eapp1e
 */
public class GlobalException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /**
     * 错误消息。
     */
    private String message;

    /**
     * 错误详情，便于内部调试或统一响应封装。
     */
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题。
     */
    public GlobalException()
    {
    }

    public GlobalException(String message)
    {
        this.message = message;
    }

    public String getDetailMessage()
    {
        return detailMessage;
    }

    public GlobalException setDetailMessage(String detailMessage)
    {
        this.detailMessage = detailMessage;
        return this;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    public GlobalException setMessage(String message)
    {
        this.message = message;
        return this;
    }
}
