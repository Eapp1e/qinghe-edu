package com.eapple.common.core.text;

import com.eapple.common.utils.StringUtils;

/**
 * 瀛楃涓叉牸寮忓寲
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
     * 鏍煎紡鍖栧瓧绗︿覆<br>
     * 姝ゆ柟娉曞彧鏄畝鍗曞皢鍗犱綅绗?{} 鎸夌収椤哄簭鏇挎崲涓哄弬鏁?br>
     * 濡傛灉鎯宠緭鍑?{} 浣跨敤 \\杞箟 { 鍗冲彲锛屽鏋滄兂杈撳嚭 {} 涔嬪墠鐨?\ 浣跨敤鍙岃浆涔夌 \\\\ 鍗冲彲<br>
     * 渚嬶細<br>
     * 閫氬父浣跨敤锛歠ormat("this is {} for {}", "a", "b") -> this is a for b<br>
     * 杞箟{}锛?format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 杞箟\锛?format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     * 
     * @param strPattern 瀛楃涓叉ā鏉?
     * @param argArray 鍙傛暟鍒楄〃
     * @return 缁撴灉
     */
    public static String format(final String strPattern, final Object... argArray)
    {
        if (StringUtils.isEmpty(strPattern) || StringUtils.isEmpty(argArray))
        {
            return strPattern;
        }
        final int strPatternLength = strPattern.length();

        // 鍒濆鍖栧畾涔夊ソ鐨勯暱搴︿互鑾峰緱鏇村ソ鐨勬€ц兘
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

        int handledPosition = 0;
        int delimIndex;// 鍗犱綅绗︽墍鍦ㄤ綅缃?
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
                { // 瀛楃涓叉ā鏉垮墿浣欓儴鍒嗕笉鍐嶅寘鍚崰浣嶇锛屽姞鍏ュ墿浣欓儴鍒嗗悗杩斿洖缁撴灉
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
                        // 杞箟绗︿箣鍓嶈繕鏈変竴涓浆涔夌锛屽崰浣嶇渚濇棫鏈夋晥
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(Convert.utf8Str(argArray[argIndex]));
                        handledPosition = delimIndex + 2;
                    }
                    else
                    {
                        // 鍗犱綅绗﹁杞箟
                        argIndex--;
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(C_DELIM_START);
                        handledPosition = delimIndex + 1;
                    }
                }
                else
                {
                    // 姝ｅ父鍗犱綅绗?
                    sbuf.append(strPattern, handledPosition, delimIndex);
                    sbuf.append(Convert.utf8Str(argArray[argIndex]));
                    handledPosition = delimIndex + 2;
                }
            }
        }
        // 鍔犲叆鏈€鍚庝竴涓崰浣嶇鍚庢墍鏈夌殑瀛楃
        sbuf.append(strPattern, handledPosition, strPattern.length());

        return sbuf.toString();
    }
}
