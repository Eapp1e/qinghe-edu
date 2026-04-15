package com.eapple.framework.web.domain.server;

import com.eapple.common.utils.Arith;

/**
 * 鍏у瓨鐩稿叧淇℃伅
 * 
 * @author Eapp1e
 */
public class Mem
{
    /**
     * 鍐呭瓨鎬婚噺
     */
    private double total;

    /**
     * 宸茬敤鍐呭瓨
     */
    private double used;

    /**
     * 鍓╀綑鍐呭瓨
     */
    private double free;

    public double getTotal()
    {
        return Arith.div(total, (1024 * 1024 * 1024), 2);
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public double getUsed()
    {
        return Arith.div(used, (1024 * 1024 * 1024), 2);
    }

    public void setUsed(long used)
    {
        this.used = used;
    }

    public double getFree()
    {
        return Arith.div(free, (1024 * 1024 * 1024), 2);
    }

    public void setFree(long free)
    {
        this.free = free;
    }

    public double getUsage()
    {
        return Arith.mul(Arith.div(used, total, 4), 100);
    }
}
