package com.eapple.common.utils.uuid;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import com.eapple.common.exception.UtilException;

/**
 * 鎻愪緵閫氱敤鍞竴璇嗗埆鐮侊紙universally unique identifier锛夛紙UUID锛夊疄鐜?
 *
 * @author Eapp1e
 */
public final class UUID implements java.io.Serializable, Comparable<UUID>
{
    private static final long serialVersionUID = -1185015143654744140L;

    /**
     * SecureRandom 鐨勫崟渚?
     *
     */
    private static class Holder
    {
        static final SecureRandom numberGenerator = getSecureRandom();
    }

    /** 姝UID鐨勬渶楂?4鏈夋晥浣?*/
    private final long mostSigBits;

    /** 姝UID鐨勬渶浣?4鏈夋晥浣?*/
    private final long leastSigBits;

    /**
     * 绉佹湁鏋勯€?
     * 
     * @param data 鏁版嵁
     */
    private UUID(byte[] data)
    {
        long msb = 0;
        long lsb = 0;
        assert data.length == 16 : "data must be 16 bytes in length";
        for (int i = 0; i < 8; i++)
        {
            msb = (msb << 8) | (data[i] & 0xff);
        }
        for (int i = 8; i < 16; i++)
        {
            lsb = (lsb << 8) | (data[i] & 0xff);
        }
        this.mostSigBits = msb;
        this.leastSigBits = lsb;
    }

    /**
     * 浣跨敤鎸囧畾鐨勬暟鎹瀯閫犳柊鐨?UUID銆?
     *
     * @param mostSigBits 鐢ㄤ簬 {@code UUID} 鐨勬渶楂樻湁鏁?64 浣?
     * @param leastSigBits 鐢ㄤ簬 {@code UUID} 鐨勬渶浣庢湁鏁?64 浣?
     */
    public UUID(long mostSigBits, long leastSigBits)
    {
        this.mostSigBits = mostSigBits;
        this.leastSigBits = leastSigBits;
    }

    /**
     * 鑾峰彇绫诲瀷 4锛堜吉闅忔満鐢熸垚鐨勶級UUID 鐨勯潤鎬佸伐鍘傘€?
     * 
     * @return 闅忔満鐢熸垚鐨?{@code UUID}
     */
    public static UUID fastUUID()
    {
        return randomUUID(false);
    }

    /**
     * 鑾峰彇绫诲瀷 4锛堜吉闅忔満鐢熸垚鐨勶級UUID 鐨勯潤鎬佸伐鍘傘€?浣跨敤鍔犲瘑鐨勫己浼殢鏈烘暟鐢熸垚鍣ㄧ敓鎴愯 UUID銆?
     * 
     * @return 闅忔満鐢熸垚鐨?{@code UUID}
     */
    public static UUID randomUUID()
    {
        return randomUUID(true);
    }

    /**
     * 鑾峰彇绫诲瀷 4锛堜吉闅忔満鐢熸垚鐨勶級UUID 鐨勯潤鎬佸伐鍘傘€?浣跨敤鍔犲瘑鐨勫己浼殢鏈烘暟鐢熸垚鍣ㄧ敓鎴愯 UUID銆?
     * 
     * @param isSecure 鏄惁浣跨敤{@link SecureRandom}濡傛灉鏄彲浠ヨ幏寰楁洿瀹夊叏鐨勯殢鏈虹爜锛屽惁鍒欏彲浠ュ緱鍒版洿濂界殑鎬ц兘
     * @return 闅忔満鐢熸垚鐨?{@code UUID}
     */
    public static UUID randomUUID(boolean isSecure)
    {
        final Random ng = isSecure ? Holder.numberGenerator : getRandom();

        byte[] randomBytes = new byte[16];
        ng.nextBytes(randomBytes);
        randomBytes[6] &= 0x0f; /* clear version */
        randomBytes[6] |= 0x40; /* set to version 4 */
        randomBytes[8] &= 0x3f; /* clear variant */
        randomBytes[8] |= 0x80; /* set to IETF variant */
        return new UUID(randomBytes);
    }

    /**
     * 鏍规嵁鎸囧畾鐨勫瓧鑺傛暟缁勮幏鍙栫被鍨?3锛堝熀浜庡悕绉扮殑锛塙UID 鐨勯潤鎬佸伐鍘傘€?
     *
     * @param name 鐢ㄤ簬鏋勯€?UUID 鐨勫瓧鑺傛暟缁勩€?
     *
     * @return 鏍规嵁鎸囧畾鏁扮粍鐢熸垚鐨?{@code UUID}
     */
    public static UUID nameUUIDFromBytes(byte[] name)
    {
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException nsae)
        {
            throw new InternalError("MD5 not supported");
        }
        byte[] md5Bytes = md.digest(name);
        md5Bytes[6] &= 0x0f; /* clear version */
        md5Bytes[6] |= 0x30; /* set to version 3 */
        md5Bytes[8] &= 0x3f; /* clear variant */
        md5Bytes[8] |= 0x80; /* set to IETF variant */
        return new UUID(md5Bytes);
    }

    /**
     * 鏍规嵁 {@link #toString()} 鏂规硶涓弿杩扮殑瀛楃涓叉爣鍑嗚〃绀哄舰寮忓垱寤簕@code UUID}銆?
     *
     * @param name 鎸囧畾 {@code UUID} 瀛楃涓?
     * @return 鍏锋湁鎸囧畾鍊肩殑 {@code UUID}
     * @throws IllegalArgumentException 濡傛灉 name 涓?{@link #toString} 涓弿杩扮殑瀛楃涓茶〃绀哄舰寮忎笉绗︽姏鍑烘寮傚父
     *
     */
    public static UUID fromString(String name)
    {
        String[] components = name.split("-");
        if (components.length != 5)
        {
            throw new IllegalArgumentException("Invalid UUID string: " + name);
        }
        for (int i = 0; i < 5; i++)
        {
            components[i] = "0x" + components[i];
        }

        long mostSigBits = Long.decode(components[0]).longValue();
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[1]).longValue();
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[2]).longValue();

        long leastSigBits = Long.decode(components[3]).longValue();
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(components[4]).longValue();

        return new UUID(mostSigBits, leastSigBits);
    }

    /**
     * 杩斿洖姝?UUID 鐨?128 浣嶅€间腑鐨勬渶浣庢湁鏁?64 浣嶃€?
     *
     * @return 姝?UUID 鐨?128 浣嶅€间腑鐨勬渶浣庢湁鏁?64 浣嶃€?
     */
    public long getLeastSignificantBits()
    {
        return leastSigBits;
    }

    /**
     * 杩斿洖姝?UUID 鐨?128 浣嶅€间腑鐨勬渶楂樻湁鏁?64 浣嶃€?
     *
     * @return 姝?UUID 鐨?128 浣嶅€间腑鏈€楂樻湁鏁?64 浣嶃€?
     */
    public long getMostSignificantBits()
    {
        return mostSigBits;
    }

    /**
     * 涓庢 {@code UUID} 鐩稿叧鑱旂殑鐗堟湰鍙? 鐗堟湰鍙锋弿杩版 {@code UUID} 鏄浣曠敓鎴愮殑銆?
     * <p>
     * 鐗堟湰鍙峰叿鏈変互涓嬪惈鎰?
     * <ul>
     * <li>1 鍩轰簬鏃堕棿鐨?UUID
     * <li>2 DCE 瀹夊叏 UUID
     * <li>3 鍩轰簬鍚嶇О鐨?UUID
     * <li>4 闅忔満鐢熸垚鐨?UUID
     * </ul>
     *
     * @return 姝?{@code UUID} 鐨勭増鏈彿
     */
    public int version()
    {
        // Version is bits masked by 0x000000000000F000 in MS long
        return (int) ((mostSigBits >> 12) & 0x0f);
    }

    /**
     * 涓庢 {@code UUID} 鐩稿叧鑱旂殑鍙樹綋鍙枫€傚彉浣撳彿鎻忚堪 {@code UUID} 鐨勫竷灞€銆?
     * <p>
     * 鍙樹綋鍙峰叿鏈変互涓嬪惈鎰忥細
     * <ul>
     * <li>0 涓?NCS 鍚戝悗鍏煎淇濈暀
     * <li>2 <a href="http://www.ietf.org/rfc/rfc4122.txt">IETF&nbsp;RFC&nbsp;4122</a>(Leach-Salz), 鐢ㄤ簬姝ょ被
     * <li>6 淇濈暀锛屽井杞悜鍚庡吋瀹?
     * <li>7 淇濈暀渚涗互鍚庡畾涔変娇鐢?
     * </ul>
     *
     * @return 姝?{@code UUID} 鐩稿叧鑱旂殑鍙樹綋鍙?
     */
    public int variant()
    {
        // This field is composed of a varying number of bits.
        // 0 - - Reserved for NCS backward compatibility
        // 1 0 - The IETF aka Leach-Salz variant (used by this class)
        // 1 1 0 Reserved, Microsoft backward compatibility
        // 1 1 1 Reserved for future definition.
        return (int) ((leastSigBits >>> (64 - (leastSigBits >>> 62))) & (leastSigBits >> 63));
    }

    /**
     * 涓庢 UUID 鐩稿叧鑱旂殑鏃堕棿鎴冲€笺€?
     *
     * <p>
     * 60 浣嶇殑鏃堕棿鎴冲€兼牴鎹 {@code UUID} 鐨?time_low銆乼ime_mid 鍜?time_hi 瀛楁鏋勯€犮€?br>
     * 鎵€寰楀埌鐨勬椂闂存埑浠?100 姣井绉掍负鍗曚綅锛屼粠 UTC锛堥€氱敤鍗忚皟鏃堕棿锛?1582 骞?10 鏈?15 鏃ラ浂鏃跺紑濮嬨€?
     *
     * <p>
     * 鏃堕棿鎴冲€间粎鍦ㄥ湪鍩轰簬鏃堕棿鐨?UUID锛堝叾 version 绫诲瀷涓?1锛変腑鎵嶆湁鎰忎箟銆?br>
     * 濡傛灉姝?{@code UUID} 涓嶆槸鍩轰簬鏃堕棿鐨?UUID锛屽垯姝ゆ柟娉曟姏鍑?UnsupportedOperationException銆?
     *
     * @throws UnsupportedOperationException 濡傛灉姝?{@code UUID} 涓嶆槸 version 涓?1 鐨?UUID銆?
     */
    public long timestamp() throws UnsupportedOperationException
    {
        checkTimeBase();
        return (mostSigBits & 0x0FFFL) << 48//
                | ((mostSigBits >> 16) & 0x0FFFFL) << 32//
                | mostSigBits >>> 32;
    }

    /**
     * 涓庢 UUID 鐩稿叧鑱旂殑鏃堕挓搴忓垪鍊笺€?
     *
     * <p>
     * 14 浣嶇殑鏃堕挓搴忓垪鍊兼牴鎹 UUID 鐨?clock_seq 瀛楁鏋勯€犮€俢lock_seq 瀛楁鐢ㄤ簬淇濊瘉鍦ㄥ熀浜庢椂闂寸殑 UUID 涓殑鏃堕棿鍞竴鎬с€?
     * <p>
     * {@code clockSequence} 鍊间粎鍦ㄥ熀浜庢椂闂寸殑 UUID锛堝叾 version 绫诲瀷涓?1锛変腑鎵嶆湁鎰忎箟銆?濡傛灉姝?UUID 涓嶆槸鍩轰簬鏃堕棿鐨?UUID锛屽垯姝ゆ柟娉曟姏鍑?
     * UnsupportedOperationException銆?
     *
     * @return 姝?{@code UUID} 鐨勬椂閽熷簭鍒?
     *
     * @throws UnsupportedOperationException 濡傛灉姝?UUID 鐨?version 涓嶄负 1
     */
    public int clockSequence() throws UnsupportedOperationException
    {
        checkTimeBase();
        return (int) ((leastSigBits & 0x3FFF000000000000L) >>> 48);
    }

    /**
     * 涓庢 UUID 鐩稿叧鐨勮妭鐐瑰€笺€?
     *
     * <p>
     * 48 浣嶇殑鑺傜偣鍊兼牴鎹 UUID 鐨?node 瀛楁鏋勯€犮€傛瀛楁鏃ㄥ湪鐢ㄤ簬淇濆瓨鏈哄櫒鐨?IEEE 802 鍦板潃锛岃鍦板潃鐢ㄤ簬鐢熸垚姝?UUID 浠ヤ繚璇佺┖闂村敮涓€鎬с€?
     * <p>
     * 鑺傜偣鍊间粎鍦ㄥ熀浜庢椂闂寸殑 UUID锛堝叾 version 绫诲瀷涓?1锛変腑鎵嶆湁鎰忎箟銆?br>
     * 濡傛灉姝?UUID 涓嶆槸鍩轰簬鏃堕棿鐨?UUID锛屽垯姝ゆ柟娉曟姏鍑?UnsupportedOperationException銆?
     *
     * @return 姝?{@code UUID} 鐨勮妭鐐瑰€?
     *
     * @throws UnsupportedOperationException 濡傛灉姝?UUID 鐨?version 涓嶄负 1
     */
    public long node() throws UnsupportedOperationException
    {
        checkTimeBase();
        return leastSigBits & 0x0000FFFFFFFFFFFFL;
    }

    /**
     * 杩斿洖姝@code UUID} 鐨勫瓧绗︿覆琛ㄧ幇褰㈠紡銆?
     *
     * <p>
     * UUID 鐨勫瓧绗︿覆琛ㄧず褰㈠紡鐢辨 BNF 鎻忚堪锛?
     * 
     * <pre>
     * {@code
     * UUID                   = <time_low>-<time_mid>-<time_high_and_version>-<variant_and_sequence>-<node>
     * time_low               = 4*<hexOctet>
     * time_mid               = 2*<hexOctet>
     * time_high_and_version  = 2*<hexOctet>
     * variant_and_sequence   = 2*<hexOctet>
     * node                   = 6*<hexOctet>
     * hexOctet               = <hexDigit><hexDigit>
     * hexDigit               = [0-9a-fA-F]
     * }
     * </pre>
     * 
     * </blockquote>
     *
     * @return 姝@code UUID} 鐨勫瓧绗︿覆琛ㄧ幇褰㈠紡
     * @see #toString(boolean)
     */
    @Override
    public String toString()
    {
        return toString(false);
    }

    /**
     * 杩斿洖姝@code UUID} 鐨勫瓧绗︿覆琛ㄧ幇褰㈠紡銆?
     *
     * <p>
     * UUID 鐨勫瓧绗︿覆琛ㄧず褰㈠紡鐢辨 BNF 鎻忚堪锛?
     * 
     * <pre>
     * {@code
     * UUID                   = <time_low>-<time_mid>-<time_high_and_version>-<variant_and_sequence>-<node>
     * time_low               = 4*<hexOctet>
     * time_mid               = 2*<hexOctet>
     * time_high_and_version  = 2*<hexOctet>
     * variant_and_sequence   = 2*<hexOctet>
     * node                   = 6*<hexOctet>
     * hexOctet               = <hexDigit><hexDigit>
     * hexDigit               = [0-9a-fA-F]
     * }
     * </pre>
     * 
     * </blockquote>
     *
     * @param isSimple 鏄惁绠€鍗曟ā寮忥紝绠€鍗曟ā寮忎负涓嶅甫'-'鐨刄UID瀛楃涓?
     * @return 姝@code UUID} 鐨勫瓧绗︿覆琛ㄧ幇褰㈠紡
     */
    public String toString(boolean isSimple)
    {
        final StringBuilder builder = new StringBuilder(isSimple ? 32 : 36);
        // time_low
        builder.append(digits(mostSigBits >> 32, 8));
        if (!isSimple)
        {
            builder.append('-');
        }
        // time_mid
        builder.append(digits(mostSigBits >> 16, 4));
        if (!isSimple)
        {
            builder.append('-');
        }
        // time_high_and_version
        builder.append(digits(mostSigBits, 4));
        if (!isSimple)
        {
            builder.append('-');
        }
        // variant_and_sequence
        builder.append(digits(leastSigBits >> 48, 4));
        if (!isSimple)
        {
            builder.append('-');
        }
        // node
        builder.append(digits(leastSigBits, 12));

        return builder.toString();
    }

    /**
     * 杩斿洖姝?UUID 鐨勫搱甯岀爜銆?
     *
     * @return UUID 鐨勫搱甯岀爜鍊笺€?
     */
    @Override
    public int hashCode()
    {
        long hilo = mostSigBits ^ leastSigBits;
        return ((int) (hilo >> 32)) ^ (int) hilo;
    }

    /**
     * 灏嗘瀵硅薄涓庢寚瀹氬璞℃瘮杈冦€?
     * <p>
     * 褰撲笖浠呭綋鍙傛暟涓嶄负 {@code null}銆佽€屾槸涓€涓?UUID 瀵硅薄銆佸叿鏈変笌姝?UUID 鐩稿悓鐨?varriant銆佸寘鍚浉鍚岀殑鍊硷紙姣忎竴浣嶅潎鐩稿悓锛夋椂锛岀粨鏋滄墠涓?{@code true}銆?
     *
     * @param obj 瑕佷笌涔嬫瘮杈冪殑瀵硅薄
     *
     * @return 濡傛灉瀵硅薄鐩稿悓锛屽垯杩斿洖 {@code true}锛涘惁鍒欒繑鍥?{@code false}
     */
    @Override
    public boolean equals(Object obj)
    {
        if ((null == obj) || (obj.getClass() != UUID.class))
        {
            return false;
        }
        UUID id = (UUID) obj;
        return (mostSigBits == id.mostSigBits && leastSigBits == id.leastSigBits);
    }

    // Comparison Operations

    /**
     * 灏嗘 UUID 涓庢寚瀹氱殑 UUID 姣旇緝銆?
     *
     * <p>
     * 濡傛灉涓や釜 UUID 涓嶅悓锛屼笖绗竴涓?UUID 鐨勬渶楂樻湁鏁堝瓧娈靛ぇ浜庣浜屼釜 UUID 鐨勫搴斿瓧娈碉紝鍒欑涓€涓?UUID 澶т簬绗簩涓?UUID銆?
     *
     * @param val 涓庢 UUID 姣旇緝鐨?UUID
     *
     * @return 鍦ㄦ UUID 灏忎簬銆佺瓑浜庢垨澶т簬 val 鏃讹紝鍒嗗埆杩斿洖 -1銆? 鎴?1銆?
     *
     */
    @Override
    public int compareTo(UUID val)
    {
        // The ordering is intentionally set up so that the UUIDs
        // can simply be numerically compared as two numbers
        return (this.mostSigBits < val.mostSigBits ? -1 : //
                (this.mostSigBits > val.mostSigBits ? 1 : //
                        (this.leastSigBits < val.leastSigBits ? -1 : //
                                (this.leastSigBits > val.leastSigBits ? 1 : //
                                        0))));
    }

    // -------------------------------------------------------------------------------------------------------------------
    // Private method start
    /**
     * 杩斿洖鎸囧畾鏁板瓧瀵瑰簲鐨刪ex鍊?
     * 
     * @param val 鍊?
     * @param digits 浣?
     * @return 鍊?
     */
    private static String digits(long val, int digits)
    {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }

    /**
     * 妫€鏌ユ槸鍚︿负time-based鐗堟湰UUID
     */
    private void checkTimeBase()
    {
        if (version() != 1)
        {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }
    }

    /**
     * 鑾峰彇{@link SecureRandom}锛岀被鎻愪緵鍔犲瘑鐨勫己闅忔満鏁扮敓鎴愬櫒 (RNG)
     * 
     * @return {@link SecureRandom}
     */
    public static SecureRandom getSecureRandom()
    {
        try
        {
            return SecureRandom.getInstance("SHA1PRNG");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new UtilException(e);
        }
    }

    /**
     * 鑾峰彇闅忔満鏁扮敓鎴愬櫒瀵硅薄<br>
     * ThreadLocalRandom鏄疛DK 7涔嬪悗鎻愪緵骞跺彂浜х敓闅忔満鏁帮紝鑳藉瑙ｅ喅澶氫釜绾跨▼鍙戠敓鐨勭珵浜変簤澶恒€?
     * 
     * @return {@link ThreadLocalRandom}
     */
    public static ThreadLocalRandom getRandom()
    {
        return ThreadLocalRandom.current();
    }
}
