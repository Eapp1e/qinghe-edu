package com.eapple.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * 前端路由对象。
 * 
 * @author Eapp1e
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo
{
    /**
     * 路由名称。
     */
    private String name;

    /**
     * 路由地址。
     */
    private String path;

    /**
     * 是否隐藏路由。true 时不会在侧边栏中展示。
     */
    private boolean hidden;

    /**
     * 重定向地址。设置为 noRedirect 时不可在面包屑中点击。
     */
    private String redirect;

    /**
     * 组件路径。
     */
    private String component;

    /**
     * 路由参数，例如 {"id": 1, "name": "ry"}。
     */
    private String query;

    /**
     * 当只有一个子路由时，是否始终显示根路由。
     */
    private Boolean alwaysShow;

    /**
     * 路由元信息。
     */
    private MetaVo meta;

    /**
     * 子路由列表。
     */
    private List<RouterVo> children;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean getHidden()
    {
        return hidden;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public String getRedirect()
    {
        return redirect;
    }

    public void setRedirect(String redirect)
    {
        this.redirect = redirect;
    }

    public String getComponent()
    {
        return component;
    }

    public void setComponent(String component)
    {
        this.component = component;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public Boolean getAlwaysShow()
    {
        return alwaysShow;
    }

    public void setAlwaysShow(Boolean alwaysShow)
    {
        this.alwaysShow = alwaysShow;
    }

    public MetaVo getMeta()
    {
        return meta;
    }

    public void setMeta(MetaVo meta)
    {
        this.meta = meta;
    }

    public List<RouterVo> getChildren()
    {
        return children;
    }

    public void setChildren(List<RouterVo> children)
    {
        this.children = children;
    }
}
