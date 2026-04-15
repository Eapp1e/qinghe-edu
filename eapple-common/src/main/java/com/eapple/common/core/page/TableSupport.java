package com.eapple.common.core.page;

import com.eapple.common.core.text.Convert;
import com.eapple.common.utils.ServletUtils;

/**
 * 琛ㄦ牸鏁版嵁澶勭悊
 * 
 * @author Eapp1e
 */
public class TableSupport
{
    /**
     * 褰撳墠璁板綍璧峰绱㈠紩
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 姣忛〉鏄剧ず璁板綍鏁?
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 鎺掑簭鍒?
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 鎺掑簭鐨勬柟鍚?"desc" 鎴栬€?"asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 鍒嗛〉鍙傛暟鍚堢悊鍖?
     */
    public static final String REASONABLE = "reasonable";

    /**
     * 灏佽鍒嗛〉瀵硅薄
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
