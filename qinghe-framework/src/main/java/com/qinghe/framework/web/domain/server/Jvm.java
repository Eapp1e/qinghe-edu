package com.qinghe.framework.web.domain.server;

import java.lang.management.ManagementFactory;
import com.qinghe.common.utils.Arith;
import com.qinghe.common.utils.DateUtils;

/**
 * JVM 信息。
 * 
 * @author Eapp1e
 */
public class Jvm
{
    /**
     * JVM 当前内存总量（M）。
     */
    private double total;

    /**
     * JVM 最大可用内存总量（M）。
     */
    private double max;

    /**
     * JVM 空闲内存（M）。
     */
    private double free;

    /**
     * JDK 版本。
     */
    private String version;

    /**
     * JDK 安装路径。
     */
    private String home;

    public double getTotal()
    {
        return Arith.div(total, (1024 * 1024), 2);
    }

    public void setTotal(double total)
    {
        this.total = total;
    }

    public double getMax()
    {
        return Arith.div(max, (1024 * 1024), 2);
    }

    public void setMax(double max)
    {
        this.max = max;
    }

    public double getFree()
    {
        return Arith.div(free, (1024 * 1024), 2);
    }

    public void setFree(double free)
    {
        this.free = free;
    }

    public double getUsed()
    {
        return Arith.div(total - free, (1024 * 1024), 2);
    }

    public double getUsage()
    {
        return Arith.mul(Arith.div(total - free, total, 4), 100);
    }

    /**
     * 获取 JDK 名称。
     */
    public String getName()
    {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getHome()
    {
        return home;
    }

    public void setHome(String home)
    {
        this.home = home;
    }

    /**
     * JDK 启动时间。
     */
    public String getStartTime()
    {
        return DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getServerStartDate());
    }

    /**
     * JDK 运行时长。
     */
    public String getRunTime()
    {
        return DateUtils.timeDistance(DateUtils.getNowDate(), DateUtils.getServerStartDate());
    }

    /**
     * 运行参数。
     */
    public String getInputArgs()
    {
        return ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
    }
}
