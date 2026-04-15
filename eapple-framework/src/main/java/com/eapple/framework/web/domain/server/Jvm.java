package com.eapple.framework.web.domain.server;

import java.lang.management.ManagementFactory;
import com.eapple.common.utils.Arith;
import com.eapple.common.utils.DateUtils;

/**
 * JVM鐩稿叧淇℃伅
 * 
 * @author Eapp1e
 */
public class Jvm
{
    /**
     * 褰撳墠JVM鍗犵敤鐨勫唴瀛樻€绘暟(M)
     */
    private double total;

    /**
     * JVM鏈€澶у彲鐢ㄥ唴瀛樻€绘暟(M)
     */
    private double max;

    /**
     * JVM绌洪棽鍐呭瓨(M)
     */
    private double free;

    /**
     * JDK鐗堟湰
     */
    private String version;

    /**
     * JDK璺緞
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
     * 鑾峰彇JDK鍚嶇О
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
     * JDK鍚姩鏃堕棿
     */
    public String getStartTime()
    {
        return DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getServerStartDate());
    }

    /**
     * JDK杩愯鏃堕棿
     */
    public String getRunTime()
    {
        return DateUtils.timeDistance(DateUtils.getNowDate(), DateUtils.getServerStartDate());
    }

    /**
     * 杩愯鍙傛暟
     */
    public String getInputArgs()
    {
        return ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
    }
}
