package com.eapple.common.core.domain;

import java.util.HashMap;
import java.util.Objects;
import com.eapple.common.constant.HttpStatus;
import com.eapple.common.utils.StringUtils;

/**
 * 统一接口返回对象。
 *
 * @author Eapp1e
 */
public class AjaxResult extends HashMap<String, Object>
{
    private static final long serialVersionUID = 1L;

    /** 状态码字段 */
    public static final String CODE_TAG = "code";

    /** 消息字段 */
    public static final String MSG_TAG = "msg";

    /** 数据字段 */
    public static final String DATA_TAG = "data";

    /**
     * 初始化一个空的返回对象。
     */
    public AjaxResult()
    {
    }

    /**
     * 初始化一个返回对象。
     *
     * @param code 状态码
     * @param msg 返回消息
     */
    public AjaxResult(int code, String msg)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个带数据的返回对象。
     *
     * @param code 状态码
     * @param msg 返回消息
     * @param data 返回数据
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
     * 返回成功结果。
     *
     * @return 成功结果
     */
    public static AjaxResult success()
    {
        return AjaxResult.success("操作成功");
    }

    /**
     * 返回成功结果并携带数据。
     *
     * @param data 返回数据
     * @return 成功结果
     */
    public static AjaxResult success(Object data)
    {
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 返回成功结果并携带消息。
     *
     * @param msg 返回消息
     * @return 成功结果
     */
    public static AjaxResult success(String msg)
    {
        return AjaxResult.success(msg, null);
    }

    /**
     * 返回成功结果并携带消息与数据。
     *
     * @param msg 返回消息
     * @param data 返回数据
     * @return 成功结果
     */
    public static AjaxResult success(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回警告结果。
     *
     * @param msg 返回消息
     * @return 警告结果
     */
    public static AjaxResult warn(String msg)
    {
        return AjaxResult.warn(msg, null);
    }

    /**
     * 返回警告结果并携带数据。
     *
     * @param msg 返回消息
     * @param data 返回数据
     * @return 警告结果
     */
    public static AjaxResult warn(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.WARN, msg, data);
    }

    /**
     * 返回失败结果。
     *
     * @return 失败结果
     */
    public static AjaxResult error()
    {
        return AjaxResult.error("操作失败");
    }

    /**
     * 返回失败结果并携带消息。
     *
     * @param msg 返回消息
     * @return 失败结果
     */
    public static AjaxResult error(String msg)
    {
        return AjaxResult.error(msg, null);
    }

    /**
     * 返回失败结果并携带消息与数据。
     *
     * @param msg 返回消息
     * @param data 返回数据
     * @return 失败结果
     */
    public static AjaxResult error(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回指定状态码的失败结果。
     *
     * @param code 状态码
     * @param msg 返回消息
     * @return 失败结果
     */
    public static AjaxResult error(int code, String msg)
    {
        return new AjaxResult(code, msg, null);
    }

    /**
     * 判断当前结果是否成功。
     *
     * @return 是否成功
     */
    public boolean isSuccess()
    {
        return Objects.equals(HttpStatus.SUCCESS, this.get(CODE_TAG));
    }

    /**
     * 判断当前结果是否为警告。
     *
     * @return 是否为警告
     */
    public boolean isWarn()
    {
        return Objects.equals(HttpStatus.WARN, this.get(CODE_TAG));
    }

    /**
     * 判断当前结果是否失败。
     *
     * @return 是否失败
     */
    public boolean isError()
    {
        return Objects.equals(HttpStatus.ERROR, this.get(CODE_TAG));
    }

    /**
     * 链式写入返回字段。
     *
     * @param key 字段名
     * @param value 字段值
     * @return 当前对象
     */
    @Override
    public AjaxResult put(String key, Object value)
    {
        super.put(key, value);
        return this;
    }
}
