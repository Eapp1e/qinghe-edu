package com.qinghe.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 高精度算术运算工具类。
 *
 * @author Eapp1e
 */
public class Arith
{
    /** 默认除法精度 */
    private static final int DEF_DIV_SCALE = 10;

    /** 工具类不允许实例化 */
    private Arith()
    {
    }

    /**
     * 提供精确加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两数之和
     */
    public static double add(double v1, double v2)
    {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两数之差
     */
    public static double sub(double v1, double v2)
    {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两数之积
     */
    public static double mul(double v1, double v2)
    {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供默认精度的除法运算。
     * 若结果无法精确表示，则按四舍五入保留默认小数位。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两数之商
     */
    public static double div(double v1, double v2)
    {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供指定精度的除法运算。
     *
     * @param v1 被除数
     * @param v2 除数
     * @param scale 保留小数位数
     * @return 两数之商
     */
    public static double div(double v1, double v2, int scale)
    {
        if (scale < 0)
        {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        if (b1.compareTo(BigDecimal.ZERO) == 0)
        {
            return BigDecimal.ZERO.doubleValue();
        }
        return b1.divide(b2, scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 对数字按指定精度进行四舍五入。
     *
     * @param v 待处理数字
     * @param scale 保留小数位数
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale)
    {
        if (scale < 0)
        {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        return b.divide(BigDecimal.ONE, scale, RoundingMode.HALF_UP).doubleValue();
    }
}
