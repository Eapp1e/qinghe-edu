package com.qinghe.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 公告已读记录对象，对应表 sys_notice_read。
 *
 * @author Eapp1e
 */
public class SysNoticeRead
{
    /** 主键 ID */
    private Long readId;

    /** 公告 ID */
    private Long noticeId;

    /** 用户 ID */
    private Long userId;

    /** 已读时间 */
    private Date readTime;

    public Long getReadId()
    {
        return readId;
    }

    public void setReadId(Long readId)
    {
        this.readId = readId;
    }

    public Long getNoticeId()
    {
        return noticeId;
    }

    public void setNoticeId(Long noticeId)
    {
        this.noticeId = noticeId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Date getReadTime()
    {
        return readTime;
    }

    public void setReadTime(Date readTime)
    {
        this.readTime = readTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("readId", getReadId())
            .append("noticeId", getNoticeId())
            .append("userId", getUserId())
            .append("readTime", getReadTime())
            .toString();
    }
}
