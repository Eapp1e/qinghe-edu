package com.eapple.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.eapple.common.core.domain.BaseEntity;
import com.eapple.common.xss.Xss;

/**
 * 閫氱煡鍏憡琛?sys_notice
 * 
 * @author Eapp1e
 */
public class SysNotice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 鍏憡ID */
    private Long noticeId;

    /** 鍏憡鏍囬 */
    private String noticeTitle;

    /** 鍏憡绫诲瀷锛?閫氱煡 2鍏憡锛?*/
    private String noticeType;

    /** 鍏憡鍐呭 */
    private String noticeContent;

    /** 鍏憡鐘舵€侊紙0姝ｅ父 1鍏抽棴锛?*/
    private String status;

    /** 鏄惁宸茶 */
    @JsonProperty("isRead")
    private boolean isRead;

    public Long getNoticeId()
    {
        return noticeId;
    }

    public void setNoticeId(Long noticeId)
    {
        this.noticeId = noticeId;
    }

    public void setNoticeTitle(String noticeTitle)
    {
        this.noticeTitle = noticeTitle;
    }

    @Xss(message = "鍏憡鏍囬涓嶈兘鍖呭惈鑴氭湰瀛楃")
    @NotBlank(message = "鍏憡鏍囬涓嶈兘涓虹┖")
    @Size(min = 0, max = 50, message = "鍏憡鏍囬涓嶈兘瓒呰繃50涓瓧绗?)
    public String getNoticeTitle()
    {
        return noticeTitle;
    }

    public void setNoticeType(String noticeType)
    {
        this.noticeType = noticeType;
    }

    public String getNoticeType()
    {
        return noticeType;
    }

    public void setNoticeContent(String noticeContent)
    {
        this.noticeContent = noticeContent;
    }

    public String getNoticeContent()
    {
        return noticeContent;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public boolean getIsRead()
    {
        return isRead;
    }

    public void setIsRead(boolean isRead)
    {
        this.isRead = isRead;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("noticeId", getNoticeId())
            .append("noticeTitle", getNoticeTitle())
            .append("noticeType", getNoticeType())
            .append("noticeContent", getNoticeContent())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
