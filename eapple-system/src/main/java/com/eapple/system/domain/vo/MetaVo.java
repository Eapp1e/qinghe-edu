package com.eapple.system.domain.vo;

import com.eapple.common.utils.StringUtils;

/**
 * 路由元信息对象。
 * 
 * @author Eapp1e
 */
public class MetaVo
{
    /**
     * 菜单标题。
     */
    private String title;

    /**
     * 菜单图标，对应前端 src/assets/icons/svg。
     */
    private String icon;

    /**
     * 是否禁用缓存，true 表示不使用 <keep-alive> 缓存。
     */
    private boolean noCache;

    /**
     * 外链地址，必须以 http(s):// 开头。
     */
    private String link;

    public MetaVo()
    {
    }

    public MetaVo(String title, String icon)
    {
        this.title = title;
        this.icon = icon;
    }

    public MetaVo(String title, String icon, boolean noCache)
    {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
    }

    public MetaVo(String title, String icon, String link)
    {
        this.title = title;
        this.icon = icon;
        this.link = link;
    }

    public MetaVo(String title, String icon, boolean noCache, String link)
    {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
        if (StringUtils.ishttp(link))
        {
            this.link = link;
        }
    }

    public boolean isNoCache()
    {
        return noCache;
    }

    public void setNoCache(boolean noCache)
    {
        this.noCache = noCache;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }
}
