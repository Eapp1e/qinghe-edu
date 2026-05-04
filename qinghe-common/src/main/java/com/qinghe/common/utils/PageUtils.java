package com.qinghe.common.utils;

import com.github.pagehelper.PageHelper;
import com.qinghe.common.core.page.PageDomain;
import com.qinghe.common.core.page.TableSupport;
import com.qinghe.common.utils.sql.SqlUtil;

/**
 * 分页工具类。
 *
 * @author Eapp1e
 */
public class PageUtils extends PageHelper
{
    /**
     * 开启分页。
     */
    public static void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

    /**
     * 清理分页上下文。
     */
    public static void clearPage()
    {
        PageHelper.clearPage();
    }
}
