package com.eapple.common.core.text;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import com.eapple.common.utils.StringUtils;

/**
 * 字符集工具类。
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
     * 转换为 Charset 对象。
     *
     * @param charset 字符集名称，空时返回系统默认字符集
     * @return Charset
     */
    public static Charset charset(String charset)
    {
        return StringUtils.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset);
    }

    /**
     * 将字符串从源字符集转换为目标字符集。
     *
     * @param source 原始字符串
     * @param srcCharset 源字符集，默认 ISO-8859-1
     * @param destCharset 目标字符集，默认 UTF-8
     * @return 转换后的字符串
     */
    public static String convert(String source, String srcCharset, String destCharset)
    {
        return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset));
    }

    /**
     * 将字符串从源字符集转换为目标字符集。
     *
     * @param source 原始字符串
     * @param srcCharset 源字符集，默认 ISO-8859-1
     * @param destCharset 目标字符集，默认 UTF-8
     * @return 转换后的字符串
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
     * 获取系统默认字符集编码。
     */
    public static String systemCharset()
    {
        return Charset.defaultCharset().name();
    }
}
