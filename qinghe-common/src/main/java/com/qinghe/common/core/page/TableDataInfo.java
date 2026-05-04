package com.qinghe.common.core.page;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象。
 *
 * @author Eapp1e
 */
public class TableDataInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 总记录数。 */
    private long total;

    /** 列表数据。 */
    private List<?> rows;

    /** 状态码。 */
    private int code;

    /** 返回消息。 */
    private String msg;

    /**
     * 表格分页数据对象。
     */
    public TableDataInfo()
    {
    }

    /**
     * 构造数据。
     *
     * @param list 列表数据
     * @param total 总记录数
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
