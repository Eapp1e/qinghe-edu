package com.eapple.common.core.domain.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.eapple.common.core.domain.BaseEntity;

/**
 * 閮ㄩ棬琛?sys_dept
 * 
 * @author Eapp1e
 */
public class SysDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 閮ㄩ棬ID */
    private Long deptId;

    /** 鐖堕儴闂↖D */
    private Long parentId;

    /** 绁栫骇鍒楄〃 */
    private String ancestors;

    /** 閮ㄩ棬鍚嶇О */
    private String deptName;

    /** 鏄剧ず椤哄簭 */
    private Integer orderNum;

    /** 璐熻矗浜?*/
    private String leader;

    /** 鑱旂郴鐢佃瘽 */
    private String phone;

    /** 閭 */
    private String email;

    /** 閮ㄩ棬鐘舵€?0姝ｅ父,1鍋滅敤 */
    private String status;

    /** 鍒犻櫎鏍囧織锛?浠ｈ〃瀛樺湪 2浠ｈ〃鍒犻櫎锛?*/
    private String delFlag;

    /** 鐖堕儴闂ㄥ悕绉?*/
    private String parentName;
    
    /** 瀛愰儴闂?*/
    private List<SysDept> children = new ArrayList<SysDept>();

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getAncestors()
    {
        return ancestors;
    }

    public void setAncestors(String ancestors)
    {
        this.ancestors = ancestors;
    }

    @NotBlank(message = "閮ㄩ棬鍚嶇О涓嶈兘涓虹┖")
    @Size(min = 0, max = 30, message = "閮ㄩ棬鍚嶇О闀垮害涓嶈兘瓒呰繃30涓瓧绗?)
    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
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

    public String getLeader()
    {
        return leader;
    }

    public void setLeader(String leader)
    {
        this.leader = leader;
    }

    @Size(min = 0, max = 11, message = "鑱旂郴鐢佃瘽闀垮害涓嶈兘瓒呰繃11涓瓧绗?)
    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    @Email(message = "閭鏍煎紡涓嶆纭?)
    @Size(min = 0, max = 50, message = "閭闀垮害涓嶈兘瓒呰繃50涓瓧绗?)
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    public List<SysDept> getChildren()
    {
        return children;
    }

    public void setChildren(List<SysDept> children)
    {
        this.children = children;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("deptId", getDeptId())
            .append("parentId", getParentId())
            .append("ancestors", getAncestors())
            .append("deptName", getDeptName())
            .append("orderNum", getOrderNum())
            .append("leader", getLeader())
            .append("phone", getPhone())
            .append("email", getEmail())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
