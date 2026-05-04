package com.qinghe.common.core.controller;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qinghe.common.constant.HttpStatus;
import com.qinghe.common.core.domain.AjaxResult;
import com.qinghe.common.core.domain.model.LoginUser;
import com.qinghe.common.core.page.PageDomain;
import com.qinghe.common.core.page.TableDataInfo;
import com.qinghe.common.core.page.TableSupport;
import com.qinghe.common.utils.DateUtils;
import com.qinghe.common.utils.PageUtils;
import com.qinghe.common.utils.SecurityUtils;
import com.qinghe.common.utils.StringUtils;
import com.qinghe.common.utils.sql.SqlUtil;

/**
 * Web 控制器基类。
 *
 * @author Eapp1e
 */
public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将日期字符串自动转换为 Date 对象。
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
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
     * 开启分页。
     */
    protected void startPage()
    {
        PageUtils.startPage();
    }

    /**
     * 开启排序。
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
     * 清理分页上下文。
     */
    protected void clearPage()
    {
        PageUtils.clearPage();
    }

    /**
     * 封装分页表格数据。
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 返回成功结果。
     */
    public AjaxResult success()
    {
        return AjaxResult.success();
    }

    /**
     * 返回失败结果。
     */
    public AjaxResult error()
    {
        return AjaxResult.error();
    }

    /**
     * 返回成功结果并携带消息。
     */
    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }

    /**
     * 返回成功结果并携带数据。
     */
    public AjaxResult success(Object data)
    {
        return AjaxResult.success(data);
    }

    /**
     * 返回失败结果并携带消息。
     */
    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }

    /**
     * 返回警告结果。
     */
    public AjaxResult warn(String message)
    {
        return AjaxResult.warn(message);
    }

    /**
     * 根据受影响行数返回执行结果。
     *
     * @param rows 受影响行数
     * @return 执行结果
     */
    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 根据布尔结果返回执行结果。
     *
     * @param result 执行结果
     * @return 响应对象
     */
    protected AjaxResult toAjax(boolean result)
    {
        return result ? success() : error();
    }

    /**
     * 返回重定向地址。
     */
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }

    /**
     * 获取当前登录用户。
     */
    public LoginUser getLoginUser()
    {
        return SecurityUtils.getLoginUser();
    }

    /**
     * 获取当前登录用户编号。
     */
    public Long getUserId()
    {
        return getLoginUser().getUserId();
    }

    /**
     * 获取当前登录用户所属部门编号。
     */
    public Long getDeptId()
    {
        return getLoginUser().getDeptId();
    }

    /**
     * 获取当前登录用户名。
     */
    public String getUsername()
    {
        return getLoginUser().getUsername();
    }
}
