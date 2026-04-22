package com.eapple.framework.web.domain.server;

import com.eapple.common.utils.Arith;

/**
 * CPU 信息。
 * 
 * @author Eapp1e
 */
public class Cpu
{
    /**
     * 核心数。
     */
    private int cpuNum;

    /**
     * CPU 总使用量。
     */
    private double total;

    /**
     * CPU 系统使用率。
     */
    private double sys;

    /**
     * CPU 用户使用率。
     */
    private double used;

    /**
     * CPU 当前等待率。
     */
    private double wait;

    /**
     * CPU 当前空闲率。
     */
    private double free;

    public int getCpuNum()
    {
        return cpuNum;
    }

    public void setCpuNum(int cpuNum)
    {
        this.cpuNum = cpuNum;
    }

    public double getTotal()
    {
        return Arith.round(Arith.mul(total, 100), 2);
    }

    public void setTotal(double total)
    {
        this.total = total;
    }

    public double getSys()
    {
        return Arith.round(Arith.mul(sys / total, 100), 2);
    }

    public void setSys(double sys)
    {
        this.sys = sys;
    }

    public double getUsed()
    {
        return Arith.round(Arith.mul(used / total, 100), 2);
    }

    public void setUsed(double used)
    {
        this.used = used;
    }

    public double getWait()
    {
        return Arith.round(Arith.mul(wait / total, 100), 2);
    }

    public void setWait(double wait)
    {
        this.wait = wait;
    }

    public double getFree()
    {
        return Arith.round(Arith.mul(free / total, 100), 2);
    }

    public void setFree(double free)
    {
        this.free = free;
    }
}
