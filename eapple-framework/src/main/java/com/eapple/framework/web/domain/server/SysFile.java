package com.eapple.framework.web.domain.server;

/**
 * 绯荤粺鏂囦欢鐩稿叧淇℃伅
 * 
 * @author Eapp1e
 */
public class SysFile
{
    /**
     * 鐩樼璺緞
     */
    private String dirName;

    /**
     * 鐩樼绫诲瀷
     */
    private String sysTypeName;

    /**
     * 鏂囦欢绫诲瀷
     */
    private String typeName;

    /**
     * 鎬诲ぇ灏?
     */
    private String total;

    /**
     * 鍓╀綑澶у皬
     */
    private String free;

    /**
     * 宸茬粡浣跨敤閲?
     */
    private String used;

    /**
     * 璧勬簮鐨勪娇鐢ㄧ巼
     */
    private double usage;

    public String getDirName()
    {
        return dirName;
    }

    public void setDirName(String dirName)
    {
        this.dirName = dirName;
    }

    public String getSysTypeName()
    {
        return sysTypeName;
    }

    public void setSysTypeName(String sysTypeName)
    {
        this.sysTypeName = sysTypeName;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public String getTotal()
    {
        return total;
    }

    public void setTotal(String total)
    {
        this.total = total;
    }

    public String getFree()
    {
        return free;
    }

    public void setFree(String free)
    {
        this.free = free;
    }

    public String getUsed()
    {
        return used;
    }

    public void setUsed(String used)
    {
        this.used = used;
    }

    public double getUsage()
    {
        return usage;
    }

    public void setUsage(double usage)
    {
        this.usage = usage;
    }
}
