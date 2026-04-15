package com.eapple.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Strings;
import org.springframework.util.AntPathMatcher;
import com.eapple.common.constant.Constants;
import com.eapple.common.core.text.StrFormatter;

/**
 * 瀛楃涓插伐鍏风被
 * 
 * @author Eapp1e
 */
@SuppressWarnings("deprecation")
public class StringUtils extends org.apache.commons.lang3.StringUtils
{
    /** 绌哄瓧绗︿覆 */
    private static final String NULLSTR = "";

    /** 涓嬪垝绾?*/
    private static final char SEPARATOR = '_';

    /** 鏄熷彿 */
    private static final char ASTERISK = '*';

    /**
     * 鑾峰彇鍙傛暟涓嶄负绌哄€?
     * 
     * @param value defaultValue 瑕佸垽鏂殑value
     * @return value 杩斿洖鍊?
     */
    public static <T> T nvl(T value, T defaultValue)
    {
        return value != null ? value : defaultValue;
    }

    /**
     * * 鍒ゆ柇涓€涓狢ollection鏄惁涓虹┖锛?鍖呭惈List锛孲et锛孮ueue
     * 
     * @param coll 瑕佸垽鏂殑Collection
     * @return true锛氫负绌?false锛氶潪绌?
     */
    public static boolean isEmpty(Collection<?> coll)
    {
        return isNull(coll) || coll.isEmpty();
    }

    /**
     * * 鍒ゆ柇涓€涓狢ollection鏄惁闈炵┖锛屽寘鍚獿ist锛孲et锛孮ueue
     * 
     * @param coll 瑕佸垽鏂殑Collection
     * @return true锛氶潪绌?false锛氱┖
     */
    public static boolean isNotEmpty(Collection<?> coll)
    {
        return !isEmpty(coll);
    }

    /**
     * * 鍒ゆ柇涓€涓璞℃暟缁勬槸鍚︿负绌?
     * 
     * @param objects 瑕佸垽鏂殑瀵硅薄鏁扮粍
     ** @return true锛氫负绌?false锛氶潪绌?
     */
    public static boolean isEmpty(Object[] objects)
    {
        return isNull(objects) || (objects.length == 0);
    }

    /**
     * * 鍒ゆ柇涓€涓璞℃暟缁勬槸鍚﹂潪绌?
     * 
     * @param objects 瑕佸垽鏂殑瀵硅薄鏁扮粍
     * @return true锛氶潪绌?false锛氱┖
     */
    public static boolean isNotEmpty(Object[] objects)
    {
        return !isEmpty(objects);
    }

    /**
     * * 鍒ゆ柇涓€涓狹ap鏄惁涓虹┖
     * 
     * @param map 瑕佸垽鏂殑Map
     * @return true锛氫负绌?false锛氶潪绌?
     */
    public static boolean isEmpty(Map<?, ?> map)
    {
        return isNull(map) || map.isEmpty();
    }

    /**
     * * 鍒ゆ柇涓€涓狹ap鏄惁涓虹┖
     * 
     * @param map 瑕佸垽鏂殑Map
     * @return true锛氶潪绌?false锛氱┖
     */
    public static boolean isNotEmpty(Map<?, ?> map)
    {
        return !isEmpty(map);
    }

    /**
     * * 鍒ゆ柇涓€涓瓧绗︿覆鏄惁涓虹┖涓?
     * 
     * @param str String
     * @return true锛氫负绌?false锛氶潪绌?
     */
    public static boolean isEmpty(String str)
    {
        return isNull(str) || NULLSTR.equals(str.trim());
    }

    /**
     * * 鍒ゆ柇涓€涓瓧绗︿覆鏄惁涓洪潪绌轰覆
     * 
     * @param str String
     * @return true锛氶潪绌轰覆 false锛氱┖涓?
     */
    public static boolean isNotEmpty(String str)
    {
        return !isEmpty(str);
    }

    /**
     * * 鍒ゆ柇涓€涓璞℃槸鍚︿负绌?
     * 
     * @param object Object
     * @return true锛氫负绌?false锛氶潪绌?
     */
    public static boolean isNull(Object object)
    {
        return object == null;
    }

    /**
     * * 鍒ゆ柇涓€涓璞℃槸鍚﹂潪绌?
     * 
     * @param object Object
     * @return true锛氶潪绌?false锛氱┖
     */
    public static boolean isNotNull(Object object)
    {
        return !isNull(object);
    }

    /**
     * * 鍒ゆ柇涓€涓璞℃槸鍚︽槸鏁扮粍绫诲瀷锛圝ava鍩烘湰鍨嬪埆鐨勬暟缁勶級
     * 
     * @param object 瀵硅薄
     * @return true锛氭槸鏁扮粍 false锛氫笉鏄暟缁?
     */
    public static boolean isArray(Object object)
    {
        return isNotNull(object) && object.getClass().isArray();
    }

    /**
     * 鍘荤┖鏍?
     */
    public static String trim(String str)
    {
        return (str == null ? "" : str.trim());
    }

    /**
     * 鏇挎崲鎸囧畾瀛楃涓茬殑鎸囧畾鍖洪棿鍐呭瓧绗︿负"*"
     *
     * @param str 瀛楃涓?
     * @param startInclude 寮€濮嬩綅缃紙鍖呭惈锛?
     * @param endExclude 缁撴潫浣嶇疆锛堜笉鍖呭惈锛?
     * @return 鏇挎崲鍚庣殑瀛楃涓?
     */
    public static String hide(CharSequence str, int startInclude, int endExclude)
    {
        if (isEmpty(str))
        {
            return NULLSTR;
        }
        final int strLength = str.length();
        if (startInclude > strLength)
        {
            return NULLSTR;
        }
        if (endExclude > strLength)
        {
            endExclude = strLength;
        }
        if (startInclude > endExclude)
        {
            // 濡傛灉璧峰浣嶇疆澶т簬缁撴潫浣嶇疆锛屼笉鏇挎崲
            return NULLSTR;
        }
        final char[] chars = new char[strLength];
        for (int i = 0; i < strLength; i++)
        {
            if (i >= startInclude && i < endExclude)
            {
                chars[i] = ASTERISK;
            }
            else
            {
                chars[i] = str.charAt(i);
            }
        }
        return new String(chars);
    }

    /**
     * 鎴彇瀛楃涓?
     * 
     * @param str 瀛楃涓?
     * @param start 寮€濮?
     * @return 缁撴灉
     */
    public static String substring(final String str, int start)
    {
        if (str == null)
        {
            return NULLSTR;
        }

        if (start < 0)
        {
            start = str.length() + start;
        }

        if (start < 0)
        {
            start = 0;
        }
        if (start > str.length())
        {
            return NULLSTR;
        }

        return str.substring(start);
    }

    /**
     * 鎴彇瀛楃涓?
     * 
     * @param str 瀛楃涓?
     * @param start 寮€濮?
     * @param end 缁撴潫
     * @return 缁撴灉
     */
    public static String substring(final String str, int start, int end)
    {
        if (str == null)
        {
            return NULLSTR;
        }

        if (end < 0)
        {
            end = str.length() + end;
        }
        if (start < 0)
        {
            start = str.length() + start;
        }

        if (end > str.length())
        {
            end = str.length();
        }

        if (start > end)
        {
            return NULLSTR;
        }

        if (start < 0)
        {
            start = 0;
        }
        if (end < 0)
        {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * 鍦ㄥ瓧绗︿覆涓煡鎵剧涓€涓嚭鐜扮殑 `open` 鍜屾渶鍚庝竴涓嚭鐜扮殑 `close` 涔嬮棿鐨勫瓙瀛楃涓?
     * 
     * @param str 瑕佹埅鍙栫殑瀛楃涓?
     * @param open 璧峰瀛楃涓?
     * @param close 缁撴潫瀛楃涓?
     * @return 鎴彇缁撴灉
     */
    public static String substringBetweenLast(final String str, final String open, final String close)
    {
        if (isEmpty(str) || isEmpty(open) || isEmpty(close))
        {
            return NULLSTR;
        }
        final int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND)
        {
            final int end = str.lastIndexOf(close);
            if (end != INDEX_NOT_FOUND)
            {
                return str.substring(start + open.length(), end);
            }
        }
        return NULLSTR;
    }

    /**
     * 鍒ゆ柇鏄惁涓虹┖锛屽苟涓斾笉鏄┖鐧藉瓧绗?
     * 
     * @param str 瑕佸垽鏂殑value
     * @return 缁撴灉
     */
    public static boolean hasText(String str)
    {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    private static boolean containsText(CharSequence str)
    {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++)
        {
            if (!Character.isWhitespace(str.charAt(i)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 鏍煎紡鍖栨枃鏈? {} 琛ㄧず鍗犱綅绗?br>
     * 姝ゆ柟娉曞彧鏄畝鍗曞皢鍗犱綅绗?{} 鎸夌収椤哄簭鏇挎崲涓哄弬鏁?br>
     * 濡傛灉鎯宠緭鍑?{} 浣跨敤 \\杞箟 { 鍗冲彲锛屽鏋滄兂杈撳嚭 {} 涔嬪墠鐨?\ 浣跨敤鍙岃浆涔夌 \\\\ 鍗冲彲<br>
     * 渚嬶細<br>
     * 閫氬父浣跨敤锛歠ormat("this is {} for {}", "a", "b") -> this is a for b<br>
     * 杞箟{}锛?format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 杞箟\锛?format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     * 
     * @param template 鏂囨湰妯℃澘锛岃鏇挎崲鐨勯儴鍒嗙敤 {} 琛ㄧず
     * @param params 鍙傛暟鍊?
     * @return 鏍煎紡鍖栧悗鐨勬枃鏈?
     */
    public static String format(String template, Object... params)
    {
        if (isEmpty(params) || isEmpty(template))
        {
            return template;
        }
        return StrFormatter.format(template, params);
    }

    /**
     * 鏄惁涓篽ttp(s)://寮€澶?
     * 
     * @param link 閾炬帴
     * @return 缁撴灉
     */
    public static boolean ishttp(String link)
    {
        return StringUtils.startsWithAny(link, Constants.HTTP, Constants.HTTPS);
    }

    /**
     * 瀛楃涓茶浆set
     * 
     * @param str 瀛楃涓?
     * @param sep 鍒嗛殧绗?
     * @return set闆嗗悎
     */
    public static final Set<String> str2Set(String str, String sep)
    {
        return new HashSet<String>(str2List(str, sep, true, false));
    }

    /**
     * 瀛楃涓茶浆list
     * 
     * @param str 瀛楃涓?
     * @param sep 鍒嗛殧绗?
     * @return list闆嗗悎
     */
    public static final List<String> str2List(String str, String sep)
    {
        return str2List(str, sep, true, false);
    }

    /**
     * 瀛楃涓茶浆list
     * 
     * @param str 瀛楃涓?
     * @param sep 鍒嗛殧绗?
     * @param filterBlank 杩囨护绾┖鐧?
     * @param trim 鍘绘帀棣栧熬绌虹櫧
     * @return list闆嗗悎
     */
    public static final List<String> str2List(String str, String sep, boolean filterBlank, boolean trim)
    {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isEmpty(str))
        {
            return list;
        }

        // 杩囨护绌虹櫧瀛楃涓?
        if (filterBlank && StringUtils.isBlank(str))
        {
            return list;
        }
        String[] split = str.split(sep);
        for (String string : split)
        {
            if (filterBlank && StringUtils.isBlank(string))
            {
                continue;
            }
            if (trim)
            {
                string = string.trim();
            }
            list.add(string);
        }

        return list;
    }

    /**
     * 妫€鏌ュ瓙瀛楃涓叉槸鍚﹀瓨鍦?
     *
     * @param seq 妫€鏌ョ殑瀛楃涓?
     * @param searchSeq 鏌ユ壘鐨勫瓧绗︿覆
     * @return 缁撴灉
     */
    public static boolean contains(final CharSequence seq, final CharSequence searchSeq)
    {
        return Strings.CS.contains(seq, searchSeq);
    }

    /**
     * 鍒ゆ柇缁欏畾鐨刢ollection鍒楄〃涓槸鍚﹀寘鍚暟缁刟rray 鍒ゆ柇缁欏畾鐨勬暟缁刟rray涓槸鍚﹀寘鍚粰瀹氱殑鍏冪礌value
     *
     * @param collection 缁欏畾鐨勯泦鍚?
     * @param array 缁欏畾鐨勬暟缁?
     * @return 缁撴灉
     */
    public static boolean containsAny(Collection<String> collection, String... array)
    {
        if (isEmpty(collection) || isEmpty(array))
        {
            return false;
        }
        else
        {
            for (String str : array)
            {
                if (collection.contains(str))
                {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 鍒ゆ柇鏄惁鍖呭惈缁欏畾鏁扮粍涓殑浠绘剰涓€涓€?
     *
     * @param cs 瑕佸垽鏂殑瀛楃涓?
     * @param searchCharSequences 瑕佸垽鏂殑鏁扮粍
     * @return 缁撴灉
     */
    public static boolean containsAny(final CharSequence cs, final CharSequence... searchCharSequences)
    {
        return Strings.CS.containsAny(cs, searchCharSequences);
    }

    /**
     * 鏌ユ壘鎸囧畾瀛楃涓叉槸鍚﹀寘鍚寚瀹氬瓧绗︿覆鍒楄〃涓殑浠绘剰涓€涓瓧绗︿覆鍚屾椂涓插拷鐣ュぇ灏忓啓
     *
     * @param cs 鎸囧畾瀛楃涓?
     * @param searchCharSequences 闇€瑕佹鏌ョ殑瀛楃涓叉暟缁?
     * @return 鏄惁鍖呭惈浠绘剰涓€涓瓧绗︿覆
     */
    public static boolean containsAnyIgnoreCase(CharSequence cs, CharSequence... searchCharSequences)
    {
        if (isEmpty(cs) || isEmpty(searchCharSequences))
        {
            return false;
        }
        for (CharSequence testStr : searchCharSequences)
        {
            if (containsIgnoreCase(cs, testStr))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 妫€鏌ユ槸鍚﹀寘鍚鎼滅储鐨勫瓧绗︿覆锛屽拷鐣ュぇ灏忓啓
     *
     * @param str 瑕佹鏌ョ殑瀛楃涓?
     * @param searchStr 瑕佹煡鎵剧殑瀛楃涓?
     * @return 濡傛灉鍖呭惈瑕佹悳绱㈢殑瀛楃涓诧紙蹇界暐澶у皬鍐欙級鍒欒繑鍥瀟rue锛屽鏋滀笉鍖呭惈鎴栬繑鍥瀎alse
     */
    public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr)
    {
        return Strings.CI.contains(str, searchStr);
    }

    /**
     * 妫€鏌ュ瓧绗︿覆鏄惁浠ヤ换鎰忓墠缂€寮€濮?
     *
     * @param sequence 瑕佹鏌ョ殑瀛楃涓?
     * @param searchStrings 鍖哄垎澶у皬鍐欑殑瀛楃涓插墠缂€鏁扮粍
     * @return 缁撴灉
     */
    public static boolean startsWithAny(final CharSequence sequence, final CharSequence... searchStrings)
    {
        return Strings.CS.startsWithAny(sequence, searchStrings);
    }

    /**
     * 涓嶅尯鍒嗗ぇ灏忓啓鍦版鏌ヤ竴涓瓧绗︿覆鏄惁浠ユ寚瀹氬墠缂€寮€澶淬€?
     *
     * @param str 寰呮鏌ョ殑瀛楃涓?
     * @param 瑕佹煡鎵剧殑鍓嶇紑
     * @return 缁撴灉
     */
    public static boolean startsWithIgnoreCase(final CharSequence str, final CharSequence prefix)
    {
        return Strings.CI.startsWith(str, prefix);
    }

    /**
     * 姣旇緝涓や釜瀛楃涓叉槸鍚︾浉鍚?
     *
     * @param cs1 绗竴涓瓧绗︿覆
     * @param cs2 绗簩涓瓧绗︿覆
     * @return 濡傛灉缁欏畾瀵硅薄涓庡瓧绗︿覆鐩哥瓑锛屽垯杩斿洖 true锛涘惁鍒欒繑鍥?false
     */
    public static boolean equals(final CharSequence cs1, final CharSequence cs2)
    {
        return Strings.CS.equals(cs1, cs2);
    }

    /**
     * 鏇挎崲瀛楃涓蹭腑鎵€鏈夊尮閰嶇殑瀛楃
     *
     * @param text 瑕佹悳绱㈠拰鏇挎崲鐨勬枃鏈?
     * @param searchString 瑕佹悳绱㈢殑瀛楃涓?
     * @param replacement  鐢ㄤ簬鏇挎崲鐨勫瓧绗︿覆
     * @return 澶勭悊瀹屾墍鏈夋浛鎹㈠悗鐨勬枃鏈?
     */
    public static String replace(final String text, final String searchString, final String replacement)
    {
        return Strings.CS.replace(text, searchString, replacement);
    }

    /**
     * 浠呭綋瀛愬瓧绗︿覆浣嶄簬婧愬瓧绗︿覆鏈熬鏃舵墠灏嗗叾绉婚櫎锛屽惁鍒欒繑鍥炴簮瀛楃涓层€?
     * @param str 瑕佹悳绱㈢殑婧愬瓧绗︿覆
     * @param remove 瑕佹悳绱㈠苟绉婚櫎鐨勫瓧绗︿覆
     * @return 濡傛灉鎵惧埌骞剁Щ闄や簡瀛楃涓诧紝鍒欒繑鍥炵Щ闄ゅ悗鐨勫瓙瀛楃涓?
     */
    public static String removeEnd(final String str, final String remove)
    {
        return Strings.CS.removeEnd(str, remove);
    }

    /**
     * 鏌ユ壘瀛楃涓查娆″嚭鐜颁綅缃殑绱㈠紩
     *
     * @param seq 瑕佹鏌ョ殑瀛楃涓?
     * @param searchSeq 瑕佹煡鎵剧殑瀛楃涓?
     * @return 杩斿洖鎸囧畾瀛楃鍦ㄥ瓧绗︿覆涓涓€娆″嚭鐜板鐨勭储寮曪紝濡傛灉姝ゅ瓧绗︿覆涓病鏈夎繖鏍风殑瀛楃锛屽垯杩斿洖 -1
     */
    public static int indexOf(final CharSequence seq, final CharSequence searchSeq)
    {
        return Strings.CS.indexOf(seq, searchSeq);
    }

    /**
     * 妫€鏌ュ瓧绗︿覆鏄惁浠ユ寚瀹氱殑鍚庣紑缁撳熬
     *
     * @param str 瑕佹鏌ョ殑瀛楃
     * @param suffix 瑕佹鏌ョ殑鍚庣紑
     * @return 鑻ュ弬鏁颁笌璇ュ瓧绗︿覆鏈熬鐩哥 true;鍚﹀垯 false
     */
    public static boolean endsWith(final CharSequence str, final CharSequence suffix)
    {
        return Strings.CS.endsWith(str, suffix);
    }

    /**
     * 灏嗙粰瀹氱殑瀛楃涓蹭笌鏁扮粍杩涜姣旇緝
     *
     * @param string 瑕佹瘮杈冪殑瀛楃涓?
     * @param searchStrings 瀛楃涓叉暟缁?
     * @return 濡傛灉瀛楃涓茬瓑浜庯紙鍖哄垎澶у皬鍐欙級{@code searchStrings}涓殑浠绘剰鍏朵粬鍏冪礌锛屽垯杩斿洖true锛涘鏋渰@code searchStrings}涓簄ull鎴栦笉鍖呭惈鍖归厤椤癸紝鍒欒繑鍥瀎alse
     */
    public static boolean equalsAny(final CharSequence string, final CharSequence... searchStrings)
    {
        return Strings.CS.equalsAny(string, searchStrings);
    }

    /**
     * 妫€鏌ヤ竴涓瓧绗︿覆鏄惁浠ヤ换鎰忔彁渚涚殑鍖哄垎澶у皬鍐欑殑鍚庣紑缁撳熬銆?
     *
     * @param sequence 瑕佹鏌ョ殑瀛楃涓?
     * @param searchStrings 瑕佹煡鎵剧殑鍖哄垎澶у皬鍐欑殑瀛楃涓叉暟缁?
     * @return 濡傛灉杈撳叆鍙傛暟{@code sequence}涓簄ull涓旀湭鎻愪緵浠讳綍{@code searchStrings}锛屾垨鑰呰緭鍏@code sequence}浠ヤ换鎰忔彁渚涚殑鍖哄垎澶у皬鍐欑殑{@code searchStrings}缁撳熬锛屽垯杩斿洖{@code true}銆?
     */
    public static boolean endsWithAny(final CharSequence sequence, final CharSequence... searchStrings)
    {
        return Strings.CS.endsWithAny(sequence, searchStrings);
    }

    /**
     * 涓嶅尯鍒嗗ぇ灏忓啓鍦版鏌ュ瓧绗﹀簭鍒楁槸鍚︿互鎸囧畾鐨勫悗缂€缁撳熬
     *
     * @param str 瑕佹鏌ョ殑瀛楃搴忓垪
     * @param suffix 瑕佹煡鎵剧殑鍚庣紑
     * @return 濡傛灉瀛楃搴忓垪浠ヨ鍚庣紑缁撳熬锛堜笉鍖哄垎澶у皬鍐欙級锛屾垨涓よ€呭潎涓簕@code null}锛屽垯杩斿洖{@code true}
     */
    public static boolean endsWithIgnoreCase(final CharSequence str, final CharSequence suffix)
    {
        return Strings.CI.endsWith(str, suffix);
    }

    /**
     * 鎸囧畾鑼冨洿鍐呮煡鎵惧瓧绗︿覆,蹇界暐澶у皬鍐?
     *
     * @param str 瑕佹鏌ョ殑瀛楃涓?
     * @param searchStr 瑕佹煡鎵剧殑瀛楃涓?
     * @return 鎼滅储瀛楃涓茬殑绗竴涓储寮曪紝濡傛灉鏈壘鍒板尮閰嶉」鍒欒繑鍥?-1
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr)
    {
        return Strings.CI.indexOf(str, searchStr);
    }

    /**
     * Compares given {@code string} to a CharSequences vararg of {@code searchStrings},
     * returning {@code true} if the {@code string} is equal to any of the {@code searchStrings}, ignoring case.
     *
     * @param string to compare, may be {@code null}.
     * @param searchStrings a vararg of strings, may be {@code null}.
     * @return {@code true} if the string is equal (case-insensitive) to any other element of {@code searchStrings};
     */
    public static boolean equalsAnyIgnoreCase(final CharSequence string, final CharSequence... searchStrings)
    {
        return Strings.CI.equalsAny(string, searchStrings);
    }

    /**
     * 椹煎嘲杞笅鍒掔嚎鍛藉悕
     */
    public static String toUnderScoreCase(String str)
    {
        if (str == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 鍓嶇疆瀛楃鏄惁澶у啓
        boolean preCharIsUpperCase = true;
        // 褰撳墠瀛楃鏄惁澶у啓
        boolean curreCharIsUpperCase = true;
        // 涓嬩竴瀛楃鏄惁澶у啓
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);
            if (i > 0)
            {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            }
            else
            {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1))
            {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase)
            {
                sb.append(SEPARATOR);
            }
            else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase)
            {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 鏄惁鍖呭惈瀛楃涓?
     * 
     * @param str 楠岃瘉瀛楃涓?
     * @param strs 瀛楃涓茬粍
     * @return 鍖呭惈杩斿洖true
     */
    public static boolean inStringIgnoreCase(String str, String... strs)
    {
        if (str != null && strs != null)
        {
            for (String s : strs)
            {
                if (str.equalsIgnoreCase(trim(s)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 鍒犻櫎鏈€鍚庝竴涓瓧绗︿覆
     *
     * @param str 杈撳叆瀛楃涓?
     * @param spit 浠ヤ粈涔堢被鍨嬬粨灏剧殑
     * @return 鎴彇鍚庣殑瀛楃涓?
     */
    public static String lastStringDel(String str, String spit)
    {
        if (!StringUtils.isEmpty(str) && str.endsWith(spit))
        {
            return str.subSequence(0, str.length() - 1).toString();
        }
        return str;
    }

    /**
     * 灏嗕笅鍒掔嚎澶у啓鏂瑰紡鍛藉悕鐨勫瓧绗︿覆杞崲涓洪┘宄板紡銆傚鏋滆浆鎹㈠墠鐨勪笅鍒掔嚎澶у啓鏂瑰紡鍛藉悕鐨勫瓧绗︿覆涓虹┖锛屽垯杩斿洖绌哄瓧绗︿覆銆?渚嬪锛欻ELLO_WORLD->HelloWorld
     * 
     * @param name 杞崲鍓嶇殑涓嬪垝绾垮ぇ鍐欐柟寮忓懡鍚嶇殑瀛楃涓?
     * @return 杞崲鍚庣殑椹煎嘲寮忓懡鍚嶇殑瀛楃涓?
     */
    public static String convertToCamelCase(String name)
    {
        StringBuilder result = new StringBuilder();
        // 蹇€熸鏌?
        if (name == null || name.isEmpty())
        {
            // 娌″繀瑕佽浆鎹?
            return "";
        }
        else if (!name.contains("_"))
        {
            // 涓嶅惈涓嬪垝绾匡紝浠呭皢棣栧瓧姣嶅ぇ鍐?
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // 鐢ㄤ笅鍒掔嚎灏嗗師濮嬪瓧绗︿覆鍒嗗壊
        String[] camels = name.split("_");
        for (String camel : camels)
        {
            // 璺宠繃鍘熷瀛楃涓蹭腑寮€澶淬€佺粨灏剧殑涓嬫崲绾挎垨鍙岄噸涓嬪垝绾?
            if (camel.isEmpty())
            {
                continue;
            }
            // 棣栧瓧姣嶅ぇ鍐?
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * 椹煎嘲寮忓懡鍚嶆硶
     * 渚嬪锛歶ser_name->userName
     */
    public static String toCamelCase(String s)
    {
        if (s == null)
        {
            return null;
        }
        if (s.indexOf(SEPARATOR) == -1)
        {
            return s;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);

            if (c == SEPARATOR)
            {
                upperCase = true;
            }
            else if (upperCase)
            {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            }
            else
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 鏌ユ壘鎸囧畾瀛楃涓叉槸鍚﹀尮閰嶆寚瀹氬瓧绗︿覆鍒楄〃涓殑浠绘剰涓€涓瓧绗︿覆
     * 
     * @param str 鎸囧畾瀛楃涓?
     * @param strs 闇€瑕佹鏌ョ殑瀛楃涓叉暟缁?
     * @return 鏄惁鍖归厤
     */
    public static boolean matches(String str, List<String> strs)
    {
        if (isEmpty(str) || isEmpty(strs))
        {
            return false;
        }
        for (String pattern : strs)
        {
            if (isMatch(pattern, str))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 鍒ゆ柇url鏄惁涓庤鍒欓厤缃? 
     * ? 琛ㄧず鍗曚釜瀛楃; 
     * * 琛ㄧず涓€灞傝矾寰勫唴鐨勪换鎰忓瓧绗︿覆锛屼笉鍙法灞傜骇; 
     * ** 琛ㄧず浠绘剰灞傝矾寰?
     * 
     * @param pattern 鍖归厤瑙勫垯
     * @param url 闇€瑕佸尮閰嶇殑url
     * @return
     */
    public static boolean isMatch(String pattern, String url)
    {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj)
    {
        return (T) obj;
    }

    /**
     * 鏁板瓧宸﹁竟琛ラ綈0锛屼娇涔嬭揪鍒版寚瀹氶暱搴︺€傛敞鎰忥紝濡傛灉鏁板瓧杞崲涓哄瓧绗︿覆鍚庯紝闀垮害澶т簬size锛屽垯鍙繚鐣?鏈€鍚巗ize涓瓧绗︺€?
     * 
     * @param num 鏁板瓧瀵硅薄
     * @param size 瀛楃涓叉寚瀹氶暱搴?
     * @return 杩斿洖鏁板瓧鐨勫瓧绗︿覆鏍煎紡锛岃瀛楃涓蹭负鎸囧畾闀垮害銆?
     */
    public static final String padl(final Number num, final int size)
    {
        return padl(num.toString(), size, '0');
    }

    /**
     * 瀛楃涓插乏琛ラ綈銆傚鏋滃師濮嬪瓧绗︿覆s闀垮害澶т簬size锛屽垯鍙繚鐣欐渶鍚巗ize涓瓧绗︺€?
     * 
     * @param s 鍘熷瀛楃涓?
     * @param size 瀛楃涓叉寚瀹氶暱搴?
     * @param c 鐢ㄤ簬琛ラ綈鐨勫瓧绗?
     * @return 杩斿洖鎸囧畾闀垮害鐨勫瓧绗︿覆锛岀敱鍘熷瓧绗︿覆宸﹁ˉ榻愭垨鎴彇寰楀埌銆?
     */
    public static final String padl(final String s, final int size, final char c)
    {
        final StringBuilder sb = new StringBuilder(size);
        if (s != null)
        {
            final int len = s.length();
            if (s.length() <= size)
            {
                for (int i = size - len; i > 0; i--)
                {
                    sb.append(c);
                }
                sb.append(s);
            }
            else
            {
                return s.substring(len - size, len);
            }
        }
        else
        {
            for (int i = size; i > 0; i--)
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}