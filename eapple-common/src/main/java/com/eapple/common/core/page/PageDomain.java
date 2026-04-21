package com.eapple.common.core.page;

import com.eapple.common.utils.StringUtils;

/**
 * 鍒嗛〉鏁版嵁
 * 
 * @author Eapp1e
 */
public class PageDomain
{
    /** 褰撳墠璁板綍璧峰绱㈠紩 */
    private Integer pageNum;

    /** 姣忛〉鏄剧ず璁板綍鏁?*/
    private Integer pageSize;

    /** 鎺掑簭鍒?*/
    private String orderByColumn;

    /** 鎺掑簭鐨勬柟鍚慸esc鎴栬€卆sc */
    private String isAsc = "asc";

    /** 鍒嗛〉鍙傛暟鍚堢悊鍖?*/
    private Boolean reasonable = true;

    public String getOrderBy()
    {
        if (StringUtils.isEmpty(orderByColumn))
        {
            return "";
        }
        return StringUtils.toUnderScoreCase(orderByColumn) + " " + isAsc;
    }

    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getOrderByColumn()
    {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn)
    {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc()
    {
        return isAsc;
    }

    public void setIsAsc(String isAsc)
    {
        if (StringUtils.isNotEmpty(isAsc))
        {
            // 鍏煎鍓嶇鎺掑簭绫诲瀷
            if ("ascending".equals(isAsc))
            {
                isAsc = "asc";
            }
            else if ("descending".equals(isAsc))
            {
                isAsc = "desc";
            }
            this.isAsc = isAsc;
        }
    }

    public Boolean getReasonable()
    {
        if (StringUtils.isNull(reasonable))
        {
            return Boolean.TRUE;
        }
        return reasonable;
    }

    public void setReasonable(Boolean reasonable)
    {
        this.reasonable = reasonable;
    }
}
