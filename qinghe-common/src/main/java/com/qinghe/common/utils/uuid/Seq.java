package com.qinghe.common.utils.uuid;

import java.util.concurrent.atomic.AtomicInteger;
import com.qinghe.common.utils.DateUtils;
import com.qinghe.common.utils.StringUtils;

/**
 * 顺序号生成器。
 *
 * @author Eapp1e
 */
public class Seq
{
    /** 通用序列类型 */
    public static final String commSeqType = "COMMON";

    /** 上传序列类型 */
    public static final String uploadSeqType = "UPLOAD";

    /** 通用序列计数器 */
    private static AtomicInteger commSeq = new AtomicInteger(1);

    /** 上传序列计数器 */
    private static AtomicInteger uploadSeq = new AtomicInteger(1);

    /** 机器标识 */
    private static final String machineCode = "A";

    /**
     * 获取默认顺序号。
     *
     * @return 顺序号
     */
    public static String getId()
    {
        return getId(commSeqType);
    }

    /**
     * 生成 16 位顺序号：yyMMddHHmmss + 机器码 + 3 位序号。
     *
     * @param type 序列类型
     * @return 顺序号
     */
    public static String getId(String type)
    {
        AtomicInteger atomicInt = commSeq;
        if (uploadSeqType.equals(type))
        {
            atomicInt = uploadSeq;
        }
        return getId(atomicInt, 3);
    }

    /**
     * 生成顺序号：yyMMddHHmmss + 机器码 + 指定位数序号。
     *
     * @param atomicInt 序列计数器
     * @param length 序号位数
     * @return 顺序号
     */
    public static String getId(AtomicInteger atomicInt, int length)
    {
        String result = DateUtils.dateTimeNow();
        result += machineCode;
        result += getSeq(atomicInt, length);
        return result;
    }

    /**
     * 生成指定长度的序号。
     *
     * @param atomicInt 序列计数器
     * @param length 序号位数
     * @return 序号字符串
     */
    private synchronized static String getSeq(AtomicInteger atomicInt, int length)
    {
        int value = atomicInt.getAndIncrement();
        int maxSeq = (int) Math.pow(10, length);
        if (atomicInt.get() >= maxSeq)
        {
            atomicInt.set(1);
        }
        return StringUtils.padl(value, length);
    }
}
