package com.eapple.common.exception;

/**
 * 涓氬姟寮傚父
 * 
 * @author Eapp1e
 */
public final class ServiceException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /**
     * 閿欒鐮?
     */
    private Integer code;

    /**
     * 閿欒鎻愮ず
     */
    private String message;

    /**
     * 閿欒鏄庣粏锛屽唴閮ㄨ皟璇曢敊璇?
     *
     * 鍜?{@link CommonResult#getDetailMessage()} 涓€鑷寸殑璁捐
     */
    private String detailMessage;

    /**
     * 绌烘瀯閫犳柟娉曪紝閬垮厤鍙嶅簭鍒楀寲闂
     */
    public ServiceException()
    {
    }

    public ServiceException(String message)
    {
        this.message = message;
    }

    public ServiceException(String message, Integer code)
    {
        this.message = message;
        this.code = code;
    }

    public String getDetailMessage()
    {
        return detailMessage;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    public Integer getCode()
    {
        return code;
    }

    public ServiceException setMessage(String message)
    {
        this.message = message;
        return this;
    }

    public ServiceException setDetailMessage(String detailMessage)
    {
        this.detailMessage = detailMessage;
        return this;
    }
}