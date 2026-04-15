package com.eapple.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.eapple.common.annotation.Excel;
import com.eapple.common.annotation.Excel.ColumnType;
import com.eapple.common.core.domain.BaseEntity;

/**
 * 鍙傛暟閰嶇疆琛?sys_config
 * 
 * @author Eapp1e
 */
public class SysConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 鍙傛暟涓婚敭 */
    @Excel(name = "鍙傛暟涓婚敭", cellType = ColumnType.NUMERIC)
    private Long configId;

    /** 鍙傛暟鍚嶇О */
    @Excel(name = "鍙傛暟鍚嶇О")
    private String configName;

    /** 鍙傛暟閿悕 */
    @Excel(name = "鍙傛暟閿悕")
    private String configKey;

    /** 鍙傛暟閿€?*/
    @Excel(name = "鍙傛暟閿€?)
    private String configValue;

    /** 绯荤粺鍐呯疆锛圷鏄?N鍚︼級 */
    @Excel(name = "绯荤粺鍐呯疆", readConverterExp = "Y=鏄?N=鍚?)
    private String configType;

    public Long getConfigId()
    {
        return configId;
    }

    public void setConfigId(Long configId)
    {
        this.configId = configId;
    }

    @NotBlank(message = "鍙傛暟鍚嶇О涓嶈兘涓虹┖")
    @Size(min = 0, max = 100, message = "鍙傛暟鍚嶇О涓嶈兘瓒呰繃100涓瓧绗?)
    public String getConfigName()
    {
        return configName;
    }

    public void setConfigName(String configName)
    {
        this.configName = configName;
    }

    @NotBlank(message = "鍙傛暟閿悕闀垮害涓嶈兘涓虹┖")
    @Size(min = 0, max = 100, message = "鍙傛暟閿悕闀垮害涓嶈兘瓒呰繃100涓瓧绗?)
    public String getConfigKey()
    {
        return configKey;
    }

    public void setConfigKey(String configKey)
    {
        this.configKey = configKey;
    }

    @NotBlank(message = "鍙傛暟閿€间笉鑳戒负绌?)
    @Size(min = 0, max = 500, message = "鍙傛暟閿€奸暱搴︿笉鑳借秴杩?00涓瓧绗?)
    public String getConfigValue()
    {
        return configValue;
    }

    public void setConfigValue(String configValue)
    {
        this.configValue = configValue;
    }

    public String getConfigType()
    {
        return configType;
    }

    public void setConfigType(String configType)
    {
        this.configType = configType;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("configId", getConfigId())
            .append("configName", getConfigName())
            .append("configKey", getConfigKey())
            .append("configValue", getConfigValue())
            .append("configType", getConfigType())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
