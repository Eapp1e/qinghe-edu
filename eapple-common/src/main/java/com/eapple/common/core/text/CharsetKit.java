package com.eapple.common.core.text;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import com.eapple.common.utils.StringUtils;

/**
 * 瀛楃闆嗗伐鍏风被
 * 
 * @author Eapp1e
 */
public class CharsetKit
{
    /** ISO-8859-1 */
    public static final String ISO_8859_1 = "ISO-8859-1";
    /** UTF-8 */
    public static final String UTF_8 = "UTF-8";
    /** GBK */
    public static final String GBK = "GBK";

    /** ISO-8859-1 */
    public static final Charset CHARSET_ISO_8859_1 = Charset.forName(ISO_8859_1);
    /** UTF-8 */
    public static final Charset CHARSET_UTF_8 = Charset.forName(UTF_8);
    /** GBK */
    public static final Charset CHARSET_GBK = Charset.forName(GBK);

    /**
     * 杞崲涓篊harset瀵硅薄
     * 
     * @param charset 瀛楃闆嗭紝涓虹┖鍒欒繑鍥為粯璁ゅ瓧绗﹂泦
     * @return Charset
     */
    public static Charset charset(String charset)
    {
        return StringUtils.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset);
    }

    /**
     * 杞崲瀛楃涓茬殑瀛楃闆嗙紪鐮?
     * 
     * @param source 瀛楃涓?
     * @param srcCharset 婧愬瓧绗﹂泦锛岄粯璁SO-8859-1
     * @param destCharset 鐩爣瀛楃闆嗭紝榛樿UTF-8
     * @return 杞崲鍚庣殑瀛楃闆?
     */
    public static String convert(String source, String srcCharset, String destCharset)
    {
        return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset));
    }

    /**
     * 杞崲瀛楃涓茬殑瀛楃闆嗙紪鐮?
     * 
     * @param source 瀛楃涓?
     * @param srcCharset 婧愬瓧绗﹂泦锛岄粯璁SO-8859-1
     * @param destCharset 鐩爣瀛楃闆嗭紝榛樿UTF-8
     * @return 杞崲鍚庣殑瀛楃闆?
     */
    public static String convert(String source, Charset srcCharset, Charset destCharset)
    {
        if (null == srcCharset)
        {
            srcCharset = StandardCharsets.ISO_8859_1;
        }

        if (null == destCharset)
        {
            destCharset = StandardCharsets.UTF_8;
        }

        if (StringUtils.isEmpty(source) || srcCharset.equals(destCharset))
        {
            return source;
        }
        return new String(source.getBytes(srcCharset), destCharset);
    }

    /**
     * @return 绯荤粺瀛楃闆嗙紪鐮?
     */
    public static String systemCharset()
    {
        return Charset.defaultCharset().name();
    }
}
