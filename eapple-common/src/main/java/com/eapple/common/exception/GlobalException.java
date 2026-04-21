package com.eapple.common.exception;

/**
 * 鍏ㄥ眬寮傚父
 * 
 * @author Eapp1e
 */
public class GlobalException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

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