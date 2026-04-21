package com.eapple.common.core.page;

import java.io.Serializable;
import java.util.List;

/**
 * 琛ㄦ牸鍒嗛〉鏁版嵁瀵硅薄
 * 
 * @author Eapp1e
 */
public class TableDataInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 鎬昏褰曟暟 */
    private long total;

    /** 鍒楄〃鏁版嵁 */
    private List<?> rows;

    /** 娑堟伅鐘舵€佺爜 */
    private int code;

    /** 娑堟伅鍐呭 */
    private String msg;

    /**
     * 琛ㄦ牸鏁版嵁瀵硅薄
     */
    public TableDataInfo()
    {
    }

    /**
     * 鍒嗛〉
     * 
     * @param list 鍒楄〃鏁版嵁
     * @param total 鎬昏褰曟暟
     */
    public TableDataInfo(List<?> list, long total)
    {
        this.rows = list;
        this.total = total;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public List<?> getRows()
    {
        return rows;
    }

    public void setRows(List<?> rows)
    {
        this.rows = rows;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }
}
