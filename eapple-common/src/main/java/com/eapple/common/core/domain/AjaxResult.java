package com.eapple.common.core.domain;

import java.util.HashMap;
import java.util.Objects;
import com.eapple.common.constant.HttpStatus;
import com.eapple.common.utils.StringUtils;

/**
 * 鎿嶄綔娑堟伅鎻愰啋
 * 
 * @author Eapp1e
 */
public class AjaxResult extends HashMap<String, Object>
{
    private static final long serialVersionUID = 1L;

    /** 鐘舵€佺爜 */
    public static final String CODE_TAG = "code";

    /** 杩斿洖鍐呭 */
    public static final String MSG_TAG = "msg";

    /** 鏁版嵁瀵硅薄 */
    public static final String DATA_TAG = "data";

    /**
     * 鍒濆鍖栦竴涓柊鍒涘缓鐨?AjaxResult 瀵硅薄锛屼娇鍏惰〃绀轰竴涓┖娑堟伅銆?
     */
    public AjaxResult()
    {
    }

    /**
     * 鍒濆鍖栦竴涓柊鍒涘缓鐨?AjaxResult 瀵硅薄
     * 
     * @param code 鐘舵€佺爜
     * @param msg 杩斿洖鍐呭
     */
    public AjaxResult(int code, String msg)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 鍒濆鍖栦竴涓柊鍒涘缓鐨?AjaxResult 瀵硅薄
     * 
     * @param code 鐘舵€佺爜
     * @param msg 杩斿洖鍐呭
     * @param data 鏁版嵁瀵硅薄
     */
    public AjaxResult(int code, String msg, Object data)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(data))
        {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 杩斿洖鎴愬姛娑堟伅
     * 
     * @return 鎴愬姛娑堟伅
     */
    public static AjaxResult success()
    {
        return AjaxResult.success("鎿嶄綔鎴愬姛");
    }

    /**
     * 杩斿洖鎴愬姛鏁版嵁
     * 
     * @return 鎴愬姛娑堟伅
     */
    public static AjaxResult success(Object data)
    {
        return AjaxResult.success("鎿嶄綔鎴愬姛", data);
    }

    /**
     * 杩斿洖鎴愬姛娑堟伅
     * 
     * @param msg 杩斿洖鍐呭
     * @return 鎴愬姛娑堟伅
     */
    public static AjaxResult success(String msg)
    {
        return AjaxResult.success(msg, null);
    }

    /**
     * 杩斿洖鎴愬姛娑堟伅
     * 
     * @param msg 杩斿洖鍐呭
     * @param data 鏁版嵁瀵硅薄
     * @return 鎴愬姛娑堟伅
     */
    public static AjaxResult success(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 杩斿洖璀﹀憡娑堟伅
     *
     * @param msg 杩斿洖鍐呭
     * @return 璀﹀憡娑堟伅
     */
    public static AjaxResult warn(String msg)
    {
        return AjaxResult.warn(msg, null);
    }

    /**
     * 杩斿洖璀﹀憡娑堟伅
     *
     * @param msg 杩斿洖鍐呭
     * @param data 鏁版嵁瀵硅薄
     * @return 璀﹀憡娑堟伅
     */
    public static AjaxResult warn(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.WARN, msg, data);
    }

    /**
     * 杩斿洖閿欒娑堟伅
     * 
     * @return 閿欒娑堟伅
     */
    public static AjaxResult error()
    {
        return AjaxResult.error("鎿嶄綔澶辫触");
    }

    /**
     * 杩斿洖閿欒娑堟伅
     * 
     * @param msg 杩斿洖鍐呭
     * @return 閿欒娑堟伅
     */
    public static AjaxResult error(String msg)
    {
        return AjaxResult.error(msg, null);
    }

    /**
     * 杩斿洖閿欒娑堟伅
     * 
     * @param msg 杩斿洖鍐呭
     * @param data 鏁版嵁瀵硅薄
     * @return 閿欒娑堟伅
     */
    public static AjaxResult error(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.ERROR, msg, data);
    }

    /**
     * 杩斿洖閿欒娑堟伅
     * 
     * @param code 鐘舵€佺爜
     * @param msg 杩斿洖鍐呭
     * @return 閿欒娑堟伅
     */
    public static AjaxResult error(int code, String msg)
    {
        return new AjaxResult(code, msg, null);
    }

    /**
     * 鏄惁涓烘垚鍔熸秷鎭?
     *
     * @return 缁撴灉
     */
    public boolean isSuccess()
    {
        return Objects.equals(HttpStatus.SUCCESS, this.get(CODE_TAG));
    }

    /**
     * 鏄惁涓鸿鍛婃秷鎭?
     *
     * @return 缁撴灉
     */
    public boolean isWarn()
    {
        return Objects.equals(HttpStatus.WARN, this.get(CODE_TAG));
    }

    /**
     * 鏄惁涓洪敊璇秷鎭?
     *
     * @return 缁撴灉
     */
    public boolean isError()
    {
        return Objects.equals(HttpStatus.ERROR, this.get(CODE_TAG));
    }

    /**
     * 鏂逛究閾惧紡璋冪敤
     *
     * @param key 閿?
     * @param value 鍊?
     * @return 鏁版嵁瀵硅薄
     */
    @Override
    public AjaxResult put(String key, Object value)
    {
        super.put(key, value);
        return this;
    }
}
