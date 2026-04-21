package com.eapple.common.core.controller;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.eapple.common.constant.HttpStatus;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.core.page.PageDomain;
import com.eapple.common.core.page.TableDataInfo;
import com.eapple.common.core.page.TableSupport;
import com.eapple.common.utils.DateUtils;
import com.eapple.common.utils.PageUtils;
import com.eapple.common.utils.SecurityUtils;
import com.eapple.common.utils.StringUtils;
import com.eapple.common.utils.sql.SqlUtil;

/**
 * web灞傞€氱敤鏁版嵁澶勭悊
 * 
 * @author Eapp1e
 */
public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 灏嗗墠鍙颁紶閫掕繃鏉ョ殑鏃ユ湡鏍煎紡鐨勫瓧绗︿覆锛岃嚜鍔ㄨ浆鍖栦负Date绫诲瀷
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 绫诲瀷杞崲
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 璁剧疆璇锋眰鍒嗛〉鏁版嵁
     */
    protected void startPage()
    {
        PageUtils.startPage();
    }

    /**
     * 璁剧疆璇锋眰鎺掑簭鏁版嵁
     */
    protected void startOrderBy()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        if (StringUtils.isNotEmpty(pageDomain.getOrderBy()))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.orderBy(orderBy);
        }
    }

    /**
     * 娓呯悊鍒嗛〉鐨勭嚎绋嬪彉閲?
     */
    protected void clearPage()
    {
        PageUtils.clearPage();
    }

    /**
     * 鍝嶅簲璇锋眰鍒嗛〉鏁版嵁
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("鏌ヨ鎴愬姛");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 杩斿洖鎴愬姛
     */
    public AjaxResult success()
    {
        return AjaxResult.success();
    }

    /**
     * 杩斿洖澶辫触娑堟伅
     */
    public AjaxResult error()
    {
        return AjaxResult.error();
    }

    /**
     * 杩斿洖鎴愬姛娑堟伅
     */
    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }
    
    /**
     * 杩斿洖鎴愬姛娑堟伅
     */
    public AjaxResult success(Object data)
    {
        return AjaxResult.success(data);
    }

    /**
     * 杩斿洖澶辫触娑堟伅
     */
    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }

    /**
     * 杩斿洖璀﹀憡娑堟伅
     */
    public AjaxResult warn(String message)
    {
        return AjaxResult.warn(message);
    }

    /**
     * 鍝嶅簲杩斿洖缁撴灉
     * 
     * @param rows 褰卞搷琛屾暟
     * @return 鎿嶄綔缁撴灉
     */
    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 鍝嶅簲杩斿洖缁撴灉
     * 
     * @param result 缁撴灉
     * @return 鎿嶄綔缁撴灉
     */
    protected AjaxResult toAjax(boolean result)
    {
        return result ? success() : error();
    }

    /**
     * 椤甸潰璺宠浆
     */
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }

    /**
     * 鑾峰彇鐢ㄦ埛缂撳瓨淇℃伅
     */
    public LoginUser getLoginUser()
    {
        return SecurityUtils.getLoginUser();
    }

    /**
     * 鑾峰彇鐧诲綍鐢ㄦ埛id
     */
    public Long getUserId()
    {
        return getLoginUser().getUserId();
    }

    /**
     * 鑾峰彇鐧诲綍閮ㄩ棬id
     */
    public Long getDeptId()
    {
        return getLoginUser().getDeptId();
    }

    /**
     * 鑾峰彇鐧诲綍鐢ㄦ埛鍚?
     */
    public String getUsername()
    {
        return getLoginUser().getUsername();
    }
}
