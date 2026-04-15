package com.eapple.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.eapple.common.annotation.Excel;
import com.eapple.common.annotation.Excel.ColumnType;
import com.eapple.common.core.domain.BaseEntity;

/**
 * 绯荤粺璁块棶璁板綍琛?sys_logininfor
 * 
 * @author Eapp1e
 */
public class SysLogininfor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Excel(name = "搴忓彿", cellType = ColumnType.NUMERIC)
    private Long infoId;

    /** 鐢ㄦ埛璐﹀彿 */
    @Excel(name = "鐢ㄦ埛璐﹀彿")
    private String userName;

    /** 鐧诲綍鐘舵€?0鎴愬姛 1澶辫触 */
    @Excel(name = "鐧诲綍鐘舵€?, readConverterExp = "0=鎴愬姛,1=澶辫触")
    private String status;

    /** 鐧诲綍IP鍦板潃 */
    @Excel(name = "鐧诲綍鍦板潃")
    private String ipaddr;

    /** 鐧诲綍鍦扮偣 */
    @Excel(name = "鐧诲綍鍦扮偣")
    private String loginLocation;

    /** 娴忚鍣ㄧ被鍨?*/
    @Excel(name = "娴忚鍣?)
    private String browser;

    /** 鎿嶄綔绯荤粺 */
    @Excel(name = "鎿嶄綔绯荤粺")
    private String os;

    /** 鎻愮ず娑堟伅 */
    @Excel(name = "鎻愮ず娑堟伅")
    private String msg;

    /** 璁块棶鏃堕棿 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "璁块棶鏃堕棿", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    public Long getInfoId()
    {
        return infoId;
    }

    public void setInfoId(Long infoId)
    {
        this.infoId = infoId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getIpaddr()
    {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr)
    {
        this.ipaddr = ipaddr;
    }

    public String getLoginLocation()
    {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation)
    {
        this.loginLocation = loginLocation;
    }

    public String getBrowser()
    {
        return browser;
    }

    public void setBrowser(String browser)
    {
        this.browser = browser;
    }

    public String getOs()
    {
        return os;
    }

    public void setOs(String os)
    {
        this.os = os;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public Date getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(Date loginTime)
    {
        this.loginTime = loginTime;
    }
}
