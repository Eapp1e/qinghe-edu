package com.eapple.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.eapple.common.annotation.Excel;
import com.eapple.common.annotation.Excel.ColumnType;
import com.eapple.common.core.domain.BaseEntity;

/**
 * 宀椾綅琛?sys_post
 * 
 * @author Eapp1e
 */
public class SysPost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 宀椾綅搴忓彿 */
    @Excel(name = "宀椾綅搴忓彿", cellType = ColumnType.NUMERIC)
    private Long postId;

    /** 宀椾綅缂栫爜 */
    @Excel(name = "宀椾綅缂栫爜")
    private String postCode;

    /** 宀椾綅鍚嶇О */
    @Excel(name = "宀椾綅鍚嶇О")
    private String postName;

    /** 宀椾綅鎺掑簭 */
    @Excel(name = "宀椾綅鎺掑簭")
    private Integer postSort;

    /** 鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?*/
    @Excel(name = "鐘舵€?, readConverterExp = "0=姝ｅ父,1=鍋滅敤")
    private String status;

    /** 鐢ㄦ埛鏄惁瀛樺湪姝ゅ矖浣嶆爣璇?榛樿涓嶅瓨鍦?*/
    private boolean flag = false;

    public Long getPostId()
    {
        return postId;
    }

    public void setPostId(Long postId)
    {
        this.postId = postId;
    }

    @NotBlank(message = "宀椾綅缂栫爜涓嶈兘涓虹┖")
    @Size(min = 0, max = 64, message = "宀椾綅缂栫爜闀垮害涓嶈兘瓒呰繃64涓瓧绗?)
    public String getPostCode()
    {
        return postCode;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    @NotBlank(message = "宀椾綅鍚嶇О涓嶈兘涓虹┖")
    @Size(min = 0, max = 50, message = "宀椾綅鍚嶇О闀垮害涓嶈兘瓒呰繃50涓瓧绗?)
    public String getPostName()
    {
        return postName;
    }

    public void setPostName(String postName)
    {
        this.postName = postName;
    }

    @NotNull(message = "鏄剧ず椤哄簭涓嶈兘涓虹┖")
    public Integer getPostSort()
    {
        return postSort;
    }

    public void setPostSort(Integer postSort)
    {
        this.postSort = postSort;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public boolean isFlag()
    {
        return flag;
    }

    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("postId", getPostId())
            .append("postCode", getPostCode())
            .append("postName", getPostName())
            .append("postSort", getPostSort())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
