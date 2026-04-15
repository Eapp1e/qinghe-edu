package com.eapple.common.utils.uuid;

/**
 * ID鐢熸垚鍣ㄥ伐鍏风被
 * 
 * @author Eapp1e
 */
public class IdUtils
{
    /**
     * 鑾峰彇闅忔満UUID
     * 
     * @return 闅忔満UUID
     */
    public static String randomUUID()
    {
        return UUID.randomUUID().toString();
    }

    /**
     * 绠€鍖栫殑UUID锛屽幓鎺変簡妯嚎
     * 
     * @return 绠€鍖栫殑UUID锛屽幓鎺変簡妯嚎
     */
    public static String simpleUUID()
    {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 鑾峰彇闅忔満UUID锛屼娇鐢ㄦ€ц兘鏇村ソ鐨凾hreadLocalRandom鐢熸垚UUID
     * 
     * @return 闅忔満UUID
     */
    public static String fastUUID()
    {
        return UUID.fastUUID().toString();
    }

    /**
     * 绠€鍖栫殑UUID锛屽幓鎺変簡妯嚎锛屼娇鐢ㄦ€ц兘鏇村ソ鐨凾hreadLocalRandom鐢熸垚UUID
     * 
     * @return 绠€鍖栫殑UUID锛屽幓鎺変簡妯嚎
     */
    public static String fastSimpleUUID()
    {
        return UUID.fastUUID().toString(true);
    }
}
