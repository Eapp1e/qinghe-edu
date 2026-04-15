package com.eapple.framework.web.domain.server;

import com.eapple.common.utils.Arith;

/**
 * CPU鐩稿叧淇℃伅
 * 
 * @author Eapp1e
 */
public class Cpu
{
    /**
     * 鏍稿績鏁?
     */
    private int cpuNum;

    /**
     * CPU鎬荤殑浣跨敤鐜?
     */
    private double total;

    /**
     * CPU绯荤粺浣跨敤鐜?
     */
    private double sys;

    /**
     * CPU鐢ㄦ埛浣跨敤鐜?
     */
    private double used;

    /**
     * CPU褰撳墠绛夊緟鐜?
     */
    private double wait;

    /**
     * CPU褰撳墠绌洪棽鐜?
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
