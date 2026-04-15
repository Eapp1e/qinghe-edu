package com.eapple.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * 璺敱閰嶇疆淇℃伅
 * 
 * @author Eapp1e
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo
{
    /**
     * 璺敱鍚嶅瓧
     */
    private String name;

    /**
     * 璺敱鍦板潃
     */
    private String path;

    /**
     * 鏄惁闅愯棌璺敱锛屽綋璁剧疆 true 鐨勬椂鍊欒璺敱涓嶄細鍐嶄晶杈规爮鍑虹幇
     */
    private boolean hidden;

    /**
     * 閲嶅畾鍚戝湴鍧€锛屽綋璁剧疆 noRedirect 鐨勬椂鍊欒璺敱鍦ㄩ潰鍖呭睉瀵艰埅涓笉鍙鐐瑰嚮
     */
    private String redirect;

    /**
     * 缁勪欢鍦板潃
     */
    private String component;

    /**
     * 璺敱鍙傛暟锛氬 {"id": 1, "name": "ry"}
     */
    private String query;

    /**
     * 褰撲綘涓€涓矾鐢变笅闈㈢殑 children 澹版槑鐨勮矾鐢卞ぇ浜?涓椂锛岃嚜鍔ㄤ細鍙樻垚宓屽鐨勬ā寮?-濡傜粍浠堕〉闈?
     */
    private Boolean alwaysShow;

    /**
     * 鍏朵粬鍏冪礌
     */
    private MetaVo meta;

    /**
     * 瀛愯矾鐢?
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
