package com.eapple.framework.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 鏁版嵁婧愬垏鎹㈠鐞?
 * 
 * @author Eapp1e
 */
public class DynamicDataSourceContextHolder
{
    public static final Logger log = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);

    /**
     * 浣跨敤ThreadLocal缁存姢鍙橀噺锛孴hreadLocal涓烘瘡涓娇鐢ㄨ鍙橀噺鐨勭嚎绋嬫彁渚涚嫭绔嬬殑鍙橀噺鍓湰锛?
     * 鎵€浠ユ瘡涓€涓嚎绋嬮兘鍙互鐙珛鍦版敼鍙樿嚜宸辩殑鍓湰锛岃€屼笉浼氬奖鍝嶅叾瀹冪嚎绋嬫墍瀵瑰簲鐨勫壇鏈€?
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 璁剧疆鏁版嵁婧愮殑鍙橀噺
     */
    public static void setDataSourceType(String dsType)
    {
        log.info("鍒囨崲鍒皗}鏁版嵁婧?, dsType);
        CONTEXT_HOLDER.set(dsType);
    }

    /**
     * 鑾峰緱鏁版嵁婧愮殑鍙橀噺
     */
    public static String getDataSourceType()
    {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 娓呯┖鏁版嵁婧愬彉閲?
     */
    public static void clearDataSourceType()
    {
        CONTEXT_HOLDER.remove();
    }
}
