package com.eapple.common.utils.html;

import com.eapple.common.utils.StringUtils;

/**
 * 杞箟鍜屽弽杞箟宸ュ叿绫?
 * 
 * @author Eapp1e
 */
public class EscapeUtil
{
    public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";

    private static final char[][] TEXT = new char[64][];

    static
    {
        for (int i = 0; i < 64; i++)
        {
            TEXT[i] = new char[] { (char) i };
        }

        // special HTML characters
        TEXT['\''] = "&#039;".toCharArray(); // 鍗曞紩鍙?
        TEXT['"'] = "&#34;".toCharArray(); // 鍙屽紩鍙?
        TEXT['&'] = "&#38;".toCharArray(); // &绗?
        TEXT['<'] = "&#60;".toCharArray(); // 灏忎簬鍙?
        TEXT['>'] = "&#62;".toCharArray(); // 澶т簬鍙?
    }

    /**
     * 杞箟鏂囨湰涓殑HTML瀛楃涓哄畨鍏ㄧ殑瀛楃
     * 
     * @param text 琚浆涔夌殑鏂囨湰
     * @return 杞箟鍚庣殑鏂囨湰
     */
    public static String escape(String text)
    {
        return encode(text);
    }

    /**
     * 杩樺師琚浆涔夌殑HTML鐗规畩瀛楃
     * 
     * @param content 鍖呭惈杞箟绗︾殑HTML鍐呭
     * @return 杞崲鍚庣殑瀛楃涓?
     */
    public static String unescape(String content)
    {
        return decode(content);
    }

    /**
     * 娓呴櫎鎵€鏈塇TML鏍囩锛屼絾鏄笉鍒犻櫎鏍囩鍐呯殑鍐呭
     * 
     * @param content 鏂囨湰
     * @return 娓呴櫎鏍囩鍚庣殑鏂囨湰
     */
    public static String clean(String content)
    {
        return new HTMLFilter().filter(content);
    }

    /**
     * Escape缂栫爜
     * 
     * @param text 琚紪鐮佺殑鏂囨湰
     * @return 缂栫爜鍚庣殑瀛楃
     */
    private static String encode(String text)
    {
        if (StringUtils.isEmpty(text))
        {
            return StringUtils.EMPTY;
        }

        final StringBuilder tmp = new StringBuilder(text.length() * 6);
        char c;
        for (int i = 0; i < text.length(); i++)
        {
            c = text.charAt(i);
            if (c < 256)
            {
                tmp.append("%");
                if (c < 16)
                {
                    tmp.append("0");
                }
                tmp.append(Integer.toString(c, 16));
            }
            else
            {
                tmp.append("%u");
                if (c <= 0xfff)
                {
                    // issue#I49JU8@Gitee
                    tmp.append("0");
                }
                tmp.append(Integer.toString(c, 16));
            }
        }
        return tmp.toString();
    }

    /**
     * Escape瑙ｇ爜
     * 
     * @param content 琚浆涔夌殑鍐呭
     * @return 瑙ｇ爜鍚庣殑瀛楃涓?
     */
    public static String decode(String content)
    {
        if (StringUtils.isEmpty(content))
        {
            return content;
        }

        StringBuilder tmp = new StringBuilder(content.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < content.length())
        {
            pos = content.indexOf("%", lastPos);
            if (pos == lastPos)
            {
                if (content.charAt(pos + 1) == 'u')
                {
                    ch = (char) Integer.parseInt(content.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                }
                else
                {
                    ch = (char) Integer.parseInt(content.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            }
            else
            {
                if (pos == -1)
                {
                    tmp.append(content.substring(lastPos));
                    lastPos = content.length();
                }
                else
                {
                    tmp.append(content.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    public static void main(String[] args)
    {
        String html = "<script>alert(1);</script>";
        String escape = EscapeUtil.escape(html);
        // String html = "<scr<script>ipt>alert(\"XSS\")</scr<script>ipt>";
        // String html = "<123";
        // String html = "123>";
        System.out.println("clean: " + EscapeUtil.clean(html));
        System.out.println("escape: " + escape);
        System.out.println("unescape: " + EscapeUtil.unescape(escape));
    }
}
