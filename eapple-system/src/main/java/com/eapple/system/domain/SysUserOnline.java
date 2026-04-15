package com.eapple.system.domain;

/**
 * 褰撳墠鍦ㄧ嚎浼氳瘽
 * 
 * @author Eapp1e
 */
public class SysUserOnline
{
    /** 浼氳瘽缂栧彿 */
    private String tokenId;

    /** 閮ㄩ棬鍚嶇О */
    private String deptName;

    /** 鐢ㄦ埛鍚嶇О */
    private String userName;

    /** 鐧诲綍IP鍦板潃 */
    private String ipaddr;

    /** 鐧诲綍鍦板潃 */
    private String loginLocation;

    /** 娴忚鍣ㄧ被鍨?*/
    private String browser;

    /** 鎿嶄綔绯荤粺 */
    private String os;

    /** 鐧诲綍鏃堕棿 */
    private Long loginTime;

    public String getTokenId()
    {
        return tokenId;
    }

    public void setTokenId(String tokenId)
    {
        this.tokenId = tokenId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
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

    public Long getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(Long loginTime)
    {
        this.loginTime = loginTime;
    }
}
