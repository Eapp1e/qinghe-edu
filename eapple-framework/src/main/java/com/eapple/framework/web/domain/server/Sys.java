package com.eapple.framework.web.domain.server;

/**
 * 绯荤粺鐩稿叧淇℃伅
 * 
 * @author Eapp1e
 */
public class Sys
{
    /**
     * 鏈嶅姟鍣ㄥ悕绉?
     */
    private String computerName;

    /**
     * 鏈嶅姟鍣↖p
     */
    private String computerIp;

    /**
     * 椤圭洰璺緞
     */
    private String userDir;

    /**
     * 鎿嶄綔绯荤粺
     */
    private String osName;

    /**
     * 绯荤粺鏋舵瀯
     */
    private String osArch;

    public String getComputerName()
    {
        return computerName;
    }

    public void setComputerName(String computerName)
    {
        this.computerName = computerName;
    }

    public String getComputerIp()
    {
        return computerIp;
    }

    public void setComputerIp(String computerIp)
    {
        this.computerIp = computerIp;
    }

    public String getUserDir()
    {
        return userDir;
    }

    public void setUserDir(String userDir)
    {
        this.userDir = userDir;
    }

    public String getOsName()
    {
        return osName;
    }

    public void setOsName(String osName)
    {
        this.osName = osName;
    }

    public String getOsArch()
    {
        return osArch;
    }

    public void setOsArch(String osArch)
    {
        this.osArch = osArch;
    }
}
