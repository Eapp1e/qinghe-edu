package com.eapple.common.utils.sql;

import com.eapple.common.exception.UtilException;
import com.eapple.common.utils.StringUtils;

/**
 * sql鎿嶄綔宸ュ叿绫?
 * 
 * @author Eapp1e
 */
public class SqlUtil
{
    /**
     * 瀹氫箟甯哥敤鐨?sql鍏抽敭瀛?
     */
    public static String SQL_REGEX = "\u000B|%0A|and |extractvalue|updatexml|sleep|information_schema|exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |or |union |like |+|/*|user()";

    /**
     * 浠呮敮鎸佸瓧姣嶃€佹暟瀛椼€佷笅鍒掔嚎銆佺┖鏍笺€侀€楀彿銆佸皬鏁扮偣锛堟敮鎸佸涓瓧娈垫帓搴忥級
     */
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    /**
     * 闄愬埗orderBy鏈€澶ч暱搴?
     */
    private static final int ORDER_BY_MAX_LENGTH = 500;

    /**
     * 妫€鏌ュ瓧绗︼紝闃叉娉ㄥ叆缁曡繃
     */
    public static String escapeOrderBySql(String value)
    {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value))
        {
            throw new UtilException("鍙傛暟涓嶇鍚堣鑼冿紝涓嶈兘杩涜鏌ヨ");
        }
        if (StringUtils.length(value) > ORDER_BY_MAX_LENGTH)
        {
            throw new UtilException("鍙傛暟宸茶秴杩囨渶澶ч檺鍒讹紝涓嶈兘杩涜鏌ヨ");
        }
        return value;
    }

    /**
     * 楠岃瘉 order by 璇硶鏄惁绗﹀悎瑙勮寖
     */
    public static boolean isValidOrderBySql(String value)
    {
        return value.matches(SQL_PATTERN);
    }

    /**
     * SQL鍏抽敭瀛楁鏌?
     */
    public static void filterKeyword(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return;
        }
        String normalizedValue = value.replaceAll("\\p{Z}|\\s", "");
        String[] sqlKeywords = StringUtils.split(SQL_REGEX, "\\|");
        for (String sqlKeyword : sqlKeywords)
        {
            if (StringUtils.indexOfIgnoreCase(normalizedValue, sqlKeyword) > -1)
            {
                throw new UtilException("璇锋眰鍙傛暟鍖呭惈鏁忔劅鍏抽敭璇?" + sqlKeyword + "'锛屽彲鑳藉瓨鍦ㄥ畨鍏ㄩ闄?);
            }
        }
    }
}
