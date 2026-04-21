package com.eapple.common.core.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree鍩虹被
 * 
 * @author Eapp1e
 */
public class TreeEntity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 鐖惰彍鍗曞悕绉?*/
    private String parentName;

    /** 鐖惰彍鍗旾D */
    private Long parentId;

    /** 鏄剧ず椤哄簭 */
    private Integer orderNum;

    /** 绁栫骇鍒楄〃 */
    private String ancestors;

    /** 瀛愰儴闂?*/
    private List<?> children = new ArrayList<>();

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

    public Integer getOrderNum()
    {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum)
    {
        this.orderNum = orderNum;
    }

    public String getAncestors()
    {
        return ancestors;
    }

    public void setAncestors(String ancestors)
    {
        this.ancestors = ancestors;
    }

    public List<?> getChildren()
    {
        return children;
    }

    public void setChildren(List<?> children)
    {
        this.children = children;
    }
}
