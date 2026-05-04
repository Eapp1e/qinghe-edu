package com.qinghe.common.core.text;

import com.qinghe.common.utils.StringUtils;

/**
 * 字符串格式化工具。
 *
 * @author Eapp1e
 */
public class StrFormatter
{
    public static final String EMPTY_JSON = "{}";
    public static final char C_BACKSLASH = '\\';
    public static final char C_DELIM_START = '{';
    public static final char C_DELIM_END = '}';

    /**
     * 简单格式化字符串模板。
     * 该方法会按顺序将占位符 {} 替换为参数。
     * 如果需要输出字面量 {}，可使用转义字符。
     *
     * 示例：
     * format("this is {} for {}", "a", "b") -> this is a for b
     * format("this is \\{} for {}", "a", "b") -> this is \{} for a
     * format("this is \\\\{} for {}", "a", "b") -> this is \a for b
     *
     * @param strPattern 模板字符串
     * @param argArray 参数列表
     * @return 格式化结果
     */
    public static String format(final String strPattern, final Object... argArray)
    {
        if (StringUtils.isEmpty(strPattern) || StringUtils.isEmpty(argArray))
        {
            return strPattern;
        }
        final int strPatternLength = strPattern.length();

        // 预留额外空间，降低扩容开销
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

        int handledPosition = 0;
        int delimIndex;
        for (int argIndex = 0; argIndex < argArray.length; argIndex++)
        {
            delimIndex = strPattern.indexOf(EMPTY_JSON, handledPosition);
            if (delimIndex == -1)
            {
                if (handledPosition == 0)
                {
                    return strPattern;
                }
                else
                {
                    // 模板剩余部分不再包含占位符，直接追加返回
                    sbuf.append(strPattern, handledPosition, strPatternLength);
                    return sbuf.toString();
                }
            }
            else
            {
                if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == C_BACKSLASH)
                {
                    if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == C_BACKSLASH)
                    {
                        // 双反斜杠场景，占位符依然有效
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(Convert.utf8Str(argArray[argIndex]));
                        handledPosition = delimIndex + 2;
                    }
                    else
                    {
                        // 占位符被转义，输出字面量 {
                        argIndex--;
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(C_DELIM_START);
                        handledPosition = delimIndex + 1;
                    }
                }
                else
                {
                    // 正常占位替换
                    sbuf.append(strPattern, handledPosition, delimIndex);
                    sbuf.append(Convert.utf8Str(argArray[argIndex]));
                    handledPosition = delimIndex + 2;
                }
            }
        }
        // 追加最后一段未处理内容
        sbuf.append(strPattern, handledPosition, strPattern.length());

        return sbuf.toString();
    }
}
