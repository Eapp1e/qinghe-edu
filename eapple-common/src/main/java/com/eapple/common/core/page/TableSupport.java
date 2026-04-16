package com.eapple.common.core.page;

import com.eapple.common.core.text.Convert;
import com.eapple.common.utils.ServletUtils;

/**
 * 表格分页数据处理工具。
 * 
 * @author Eapp1e
 */
public class TableSupport
{
    /**
     * 当前记录起始索引。
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数。
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列。
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序方向，"desc" 或者 "asc"。
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 是否启用合理化分页。
     */
    public static final String REASONABLE = "reasonable";

    /**
     * 构建分页请求对象。
     */
    public static PageDomain getPageDomain()
    {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(Convert.toInt(ServletUtils.getParameter(PAGE_NUM), 1));
        pageDomain.setPageSize(Convert.toInt(ServletUtils.getParameter(PAGE_SIZE), 10));
        pageDomain.setOrderByColumn(ServletUtils.getParameter(ORDER_BY_COLUMN));
        pageDomain.setIsAsc(ServletUtils.getParameter(IS_ASC));
        pageDomain.setReasonable(ServletUtils.getParameterToBool(REASONABLE));
        return pageDomain;
    }

    public static PageDomain buildPageRequest()
    {
        return getPageDomain();
    }
}