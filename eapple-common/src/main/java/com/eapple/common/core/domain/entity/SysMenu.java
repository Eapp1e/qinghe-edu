package com.eapple.common.core.domain.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.eapple.common.core.domain.BaseEntity;

/**
 * 鑿滃崟鏉冮檺琛?sys_menu
 * 
 * @author Eapp1e
 */
public class SysMenu extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 鑿滃崟ID */
    private Long menuId;

    /** 鑿滃崟鍚嶇О */
    private String menuName;

    /** 鐖惰彍鍗曞悕绉?*/
    private String parentName;

    /** 鐖惰彍鍗旾D */
    private Long parentId;

    /** 鏄剧ず椤哄簭 */
    private Integer orderNum;

    /** 璺敱鍦板潃 */
    private String path;

    /** 缁勪欢璺緞 */
    private String component;

    /** 璺敱鍙傛暟 */
    private String query;

    /** 璺敱鍚嶇О锛岄粯璁ゅ拰璺敱鍦板潃鐩稿悓鐨勯┘宄版牸寮忥紙娉ㄦ剰锛氬洜涓簐ue3鐗堟湰鐨剅outer浼氬垹闄ゅ悕绉扮浉鍚岃矾鐢憋紝涓洪伩鍏嶅悕瀛楃殑鍐茬獊锛岀壒娈婃儏鍐靛彲浠ヨ嚜瀹氫箟锛?*/
    private String routeName;

    /** 鏄惁涓哄閾撅紙0鏄?1鍚︼級 */
    private String isFrame;

    /** 鏄惁缂撳瓨锛?缂撳瓨 1涓嶇紦瀛橈級 */
    private String isCache;

    /** 绫诲瀷锛圡鐩綍 C鑿滃崟 F鎸夐挳锛?*/
    private String menuType;

    /** 鏄剧ず鐘舵€侊紙0鏄剧ず 1闅愯棌锛?*/
    private String visible;

    /** 鑿滃崟鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?*/
    private String status;

    /** 鏉冮檺瀛楃涓?*/
    private String perms;

    /** 鑿滃崟鍥炬爣 */
    private String icon;

    /** 瀛愯彍鍗?*/
    private List<SysMenu> children = new ArrayList<SysMenu>();

    public Long getMenuId()
    {
        return menuId;
    }

    public void setMenuId(Long menuId)
    {
        this.menuId = menuId;
    }

    @NotBlank(message = "鑿滃崟鍚嶇О涓嶈兘涓虹┖")
    @Size(min = 0, max = 50, message = "鑿滃崟鍚嶇О闀垮害涓嶈兘瓒呰繃50涓瓧绗?)
    public String getMenuName()
    {
        return menuName;
    }

    public void setMenuName(String menuName)
    {
        this.menuName = menuName;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    @NotNull(message = "鏄剧ず椤哄簭涓嶈兘涓虹┖")
    public Integer getOrderNum()
    {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum)
    {
        this.orderNum = orderNum;
    }

    @Size(min = 0, max = 200, message = "璺敱鍦板潃涓嶈兘瓒呰繃200涓瓧绗?)
    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @Size(min = 0, max = 200, message = "缁勪欢璺緞涓嶈兘瓒呰繃255涓瓧绗?)
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

    public String getRouteName()
    {
        return routeName;
    }

    public void setRouteName(String routeName)
    {
        this.routeName = routeName;
    }

    public String getIsFrame()
    {
        return isFrame;
    }

    public void setIsFrame(String isFrame)
    {
        this.isFrame = isFrame;
    }

    public String getIsCache()
    {
        return isCache;
    }

    public void setIsCache(String isCache)
    {
        this.isCache = isCache;
    }

    @NotBlank(message = "鑿滃崟绫诲瀷涓嶈兘涓虹┖")
    public String getMenuType()
    {
        return menuType;
    }

    public void setMenuType(String menuType)
    {
        this.menuType = menuType;
    }

    public String getVisible()
    {
        return visible;
    }

    public void setVisible(String visible)
    {
        this.visible = visible;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Size(min = 0, max = 100, message = "鏉冮檺鏍囪瘑闀垮害涓嶈兘瓒呰繃100涓瓧绗?)
    public String getPerms()
    {
        return perms;
    }

    public void setPerms(String perms)
    {
        this.perms = perms;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public List<SysMenu> getChildren()
    {
        return children;
    }

    public void setChildren(List<SysMenu> children)
    {
        this.children = children;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("menuId", getMenuId())
            .append("menuName", getMenuName())
            .append("parentId", getParentId())
            .append("orderNum", getOrderNum())
            .append("path", getPath())
            .append("component", getComponent())
            .append("query", getQuery())
            .append("routeName", getRouteName())
            .append("isFrame", getIsFrame())
            .append("IsCache", getIsCache())
            .append("menuType", getMenuType())
            .append("visible", getVisible())
            .append("status ", getStatus())
            .append("perms", getPerms())
            .append("icon", getIcon())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
