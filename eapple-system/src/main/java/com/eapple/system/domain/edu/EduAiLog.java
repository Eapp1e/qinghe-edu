package com.eapple.system.domain.edu;

import com.eapple.common.annotation.Excel;
import com.eapple.common.core.domain.BaseEntity;

public class EduAiLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long logId;
    private String businessType;
    private Long bizId;
    private Long userId;

    @Excel(name = "鐢ㄦ埛")
    private String userName;

    @Excel(name = "瑙掕壊")
    private String roleType;

    @Excel(name = "鎻愮ず璇?)
    private String promptContent;

    @Excel(name = "杩斿洖鍐呭")
    private String responseContent;

    @Excel(name = "妯″瀷")
    private String modelName;

    @Excel(name = "鐘舵€?)
    private String status;

    @Excel(name = "椋庨櫓鏍囪")
    private String riskFlag;

    private String errorMessage;
    private Integer promptTokens;
    private Integer completionTokens;
    private Long latencyMs;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public String getBusinessType()
    {
        return businessType;
    }

    public void setBusinessType(String businessType)
    {
        this.businessType = businessType;
    }

    public Long getBizId()
    {
        return bizId;
    }

    public void setBizId(Long bizId)
    {
        this.bizId = bizId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getRoleType()
    {
        return roleType;
    }

    public void setRoleType(String roleType)
    {
        this.roleType = roleType;
    }

    public String getPromptContent()
    {
        return promptContent;
    }

    public void setPromptContent(String promptContent)
    {
        this.promptContent = promptContent;
    }

    public String getResponseContent()
    {
        return responseContent;
    }

    public void setResponseContent(String responseContent)
    {
        this.responseContent = responseContent;
    }

    public String getModelName()
    {
        return modelName;
    }

    public void setModelName(String modelName)
    {
        this.modelName = modelName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRiskFlag()
    {
        return riskFlag;
    }

    public void setRiskFlag(String riskFlag)
    {
        this.riskFlag = riskFlag;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public Integer getPromptTokens()
    {
        return promptTokens;
    }

    public void setPromptTokens(Integer promptTokens)
    {
        this.promptTokens = promptTokens;
    }

    public Integer getCompletionTokens()
    {
        return completionTokens;
    }

    public void setCompletionTokens(Integer completionTokens)
    {
        this.completionTokens = completionTokens;
    }

    public Long getLatencyMs()
    {
        return latencyMs;
    }

    public void setLatencyMs(Long latencyMs)
    {
        this.latencyMs = latencyMs;
    }
}
