package com.eapple.common.utils.uuid;

import java.util.concurrent.atomic.AtomicInteger;
import com.eapple.common.utils.DateUtils;
import com.eapple.common.utils.StringUtils;

/**
 * @author Eapp1e 搴忓垪鐢熸垚绫?
 */
public class Seq
{
    // 閫氱敤搴忓垪绫诲瀷
    public static final String commSeqType = "COMMON";

    // 涓婁紶搴忓垪绫诲瀷
    public static final String uploadSeqType = "UPLOAD";

    // 閫氱敤鎺ュ彛搴忓垪鏁?
    private static AtomicInteger commSeq = new AtomicInteger(1);

    // 涓婁紶鎺ュ彛搴忓垪鏁?
    private static AtomicInteger uploadSeq = new AtomicInteger(1);

    // 鏈哄櫒鏍囪瘑
    private static final String machineCode = "A";

    /**
     * 鑾峰彇閫氱敤搴忓垪鍙?
     * 
     * @return 搴忓垪鍊?
     */
    public static String getId()
    {
        return getId(commSeqType);
    }
    
    /**
     * 榛樿16浣嶅簭鍒楀彿 yyMMddHHmmss + 涓€浣嶆満鍣ㄦ爣璇?+ 3闀垮害寰幆閫掑瀛楃涓?
     * 
     * @return 搴忓垪鍊?
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
     * 閫氱敤鎺ュ彛搴忓垪鍙?yyMMddHHmmss + 涓€浣嶆満鍣ㄦ爣璇?+ length闀垮害寰幆閫掑瀛楃涓?
     * 
     * @param atomicInt 搴忓垪鏁?
     * @param length 鏁板€奸暱搴?
     * @return 搴忓垪鍊?
     */
    public static String getId(AtomicInteger atomicInt, int length)
    {
        String result = DateUtils.dateTimeNow();
        result += machineCode;
        result += getSeq(atomicInt, length);
        return result;
    }

    /**
     * 搴忓垪寰幆閫掑瀛楃涓瞇1, 10 鐨?(length)骞傛鏂?, 鐢?宸﹁ˉ榻恖ength浣嶆暟
     * 
     * @return 搴忓垪鍊?
     */
    private synchronized static String getSeq(AtomicInteger atomicInt, int length)
    {
        // 鍏堝彇鍊煎啀+1
        int value = atomicInt.getAndIncrement();

        // 濡傛灉鏇存柊鍚庡€?=10 鐨?(length)骞傛鏂瑰垯閲嶇疆涓?
        int maxSeq = (int) Math.pow(10, length);
        if (atomicInt.get() >= maxSeq)
        {
            atomicInt.set(1);
        }
        // 杞瓧绗︿覆锛岀敤0宸﹁ˉ榻?
        return StringUtils.padl(value, length);
    }
}
