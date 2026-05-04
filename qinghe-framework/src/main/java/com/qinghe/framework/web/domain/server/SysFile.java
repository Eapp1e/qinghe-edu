package com.qinghe.framework.web.domain.server;

/**
 * 系统磁盘相关信息。
 * 
 * @author Eapp1e
 */
public class SysFile
{
    /**
     * 盘符路径。
     */
    private String dirName;

    /**
     * 盘符类型。
     */
    private String sysTypeName;

    /**
     * 文件系统类型。
     */
    private String typeName;

    /**
     * 总大小。
     */
    private String total;

    /**
     * 剩余大小。
     */
    private String free;

    /**
     * 已用大小。
     */
    private String used;

    /**
     * 资源使用率。
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
