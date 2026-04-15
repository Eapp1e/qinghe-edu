package com.eapple.common.core.domain.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.eapple.common.annotation.Excel;
import com.eapple.common.annotation.Excel.ColumnType;
import com.eapple.common.core.domain.BaseEntity;

/**
 * 瀛楀吀绫诲瀷琛?sys_dict_type
 * 
 * @author Eapp1e
 */
public class SysDictType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 瀛楀吀涓婚敭 */
    @Excel(name = "瀛楀吀涓婚敭", cellType = ColumnType.NUMERIC)
    private Long dictId;

    /** 瀛楀吀鍚嶇О */
    @Excel(name = "瀛楀吀鍚嶇О")
    private String dictName;

    /** 瀛楀吀绫诲瀷 */
    @Excel(name = "瀛楀吀绫诲瀷")
    private String dictType;

    /** 鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?*/
    @Excel(name = "鐘舵€?, readConverterExp = "0=姝ｅ父,1=鍋滅敤")
    private String status;

    public Long getDictId()
    {
        return dictId;
    }

    public void setDictId(Long dictId)
    {
        this.dictId = dictId;
    }

    @NotBlank(message = "瀛楀吀鍚嶇О涓嶈兘涓虹┖")
    @Size(min = 0, max = 100, message = "瀛楀吀绫诲瀷鍚嶇О闀垮害涓嶈兘瓒呰繃100涓瓧绗?)
    public String getDictName()
    {
        return dictName;
    }

    public void setDictName(String dictName)
    {
        this.dictName = dictName;
    }

    @NotBlank(message = "瀛楀吀绫诲瀷涓嶈兘涓虹┖")
    @Size(min = 0, max = 100, message = "瀛楀吀绫诲瀷绫诲瀷闀垮害涓嶈兘瓒呰繃100涓瓧绗?)
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "瀛楀吀绫诲瀷蹇呴』浠ュ瓧姣嶅紑澶达紝涓斿彧鑳戒负锛堝皬鍐欏瓧姣嶏紝鏁板瓧锛屼笅婊戠嚎锛?)
    public String getDictType()
    {
        return dictType;
    }

    public void setDictType(String dictType)
    {
        this.dictType = dictType;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("dictId", getDictId())
            .append("dictName", getDictName())
            .append("dictType", getDictType())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
