package com.eapple.common.core.domain.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.eapple.common.annotation.Excel;
import com.eapple.common.annotation.Excel.ColumnType;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.domain.BaseEntity;

/**
 * 瀛楀吀鏁版嵁琛?sys_dict_data
 * 
 * @author Eapp1e
 */
public class SysDictData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 瀛楀吀缂栫爜 */
    @Excel(name = "瀛楀吀缂栫爜", cellType = ColumnType.NUMERIC)
    private Long dictCode;

    /** 瀛楀吀鎺掑簭 */
    @Excel(name = "瀛楀吀鎺掑簭", cellType = ColumnType.NUMERIC)
    private Long dictSort;

    /** 瀛楀吀鏍囩 */
    @Excel(name = "瀛楀吀鏍囩")
    private String dictLabel;

    /** 瀛楀吀閿€?*/
    @Excel(name = "瀛楀吀閿€?)
    private String dictValue;

    /** 瀛楀吀绫诲瀷 */
    @Excel(name = "瀛楀吀绫诲瀷")
    private String dictType;

    /** 鏍峰紡灞炴€э紙鍏朵粬鏍峰紡鎵╁睍锛?*/
    private String cssClass;

    /** 琛ㄦ牸瀛楀吀鏍峰紡 */
    private String listClass;

    /** 鏄惁榛樿锛圷鏄?N鍚︼級 */
    @Excel(name = "鏄惁榛樿", readConverterExp = "Y=鏄?N=鍚?)
    private String isDefault;

    /** 鐘舵€侊紙0姝ｅ父 1鍋滅敤锛?*/
    @Excel(name = "鐘舵€?, readConverterExp = "0=姝ｅ父,1=鍋滅敤")
    private String status;

    public Long getDictCode()
    {
        return dictCode;
    }

    public void setDictCode(Long dictCode)
    {
        this.dictCode = dictCode;
    }

    public Long getDictSort()
    {
        return dictSort;
    }

    public void setDictSort(Long dictSort)
    {
        this.dictSort = dictSort;
    }

    @NotBlank(message = "瀛楀吀鏍囩涓嶈兘涓虹┖")
    @Size(min = 0, max = 100, message = "瀛楀吀鏍囩闀垮害涓嶈兘瓒呰繃100涓瓧绗?)
    public String getDictLabel()
    {
        return dictLabel;
    }

    public void setDictLabel(String dictLabel)
    {
        this.dictLabel = dictLabel;
    }

    @NotBlank(message = "瀛楀吀閿€间笉鑳戒负绌?)
    @Size(min = 0, max = 100, message = "瀛楀吀閿€奸暱搴︿笉鑳借秴杩?00涓瓧绗?)
    public String getDictValue()
    {
        return dictValue;
    }

    public void setDictValue(String dictValue)
    {
        this.dictValue = dictValue;
    }

    @NotBlank(message = "瀛楀吀绫诲瀷涓嶈兘涓虹┖")
    @Size(min = 0, max = 100, message = "瀛楀吀绫诲瀷闀垮害涓嶈兘瓒呰繃100涓瓧绗?)
    public String getDictType()
    {
        return dictType;
    }

    public void setDictType(String dictType)
    {
        this.dictType = dictType;
    }

    @Size(min = 0, max = 100, message = "鏍峰紡灞炴€ч暱搴︿笉鑳借秴杩?00涓瓧绗?)
    public String getCssClass()
    {
        return cssClass;
    }

    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }

    public String getListClass()
    {
        return listClass;
    }

    public void setListClass(String listClass)
    {
        this.listClass = listClass;
    }

    public boolean getDefault()
    {
        return UserConstants.YES.equals(this.isDefault);
    }

    public String getIsDefault()
    {
        return isDefault;
    }

    public void setIsDefault(String isDefault)
    {
        this.isDefault = isDefault;
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
            .append("dictCode", getDictCode())
            .append("dictSort", getDictSort())
            .append("dictLabel", getDictLabel())
            .append("dictValue", getDictValue())
            .append("dictType", getDictType())
            .append("cssClass", getCssClass())
            .append("listClass", getListClass())
            .append("isDefault", getIsDefault())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
