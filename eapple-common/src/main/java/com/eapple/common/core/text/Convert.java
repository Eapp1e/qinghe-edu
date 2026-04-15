package com.eapple.common.core.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Set;
import com.eapple.common.utils.StringUtils;

/**
 * 绫诲瀷杞崲鍣?
 *
 * @author Eapp1e
 */
public class Convert
{
    /**
     * 杞崲涓哄瓧绗︿覆<br>
     * 濡傛灉缁欏畾鐨勫€间负null锛屾垨鑰呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @param defaultValue 杞崲閿欒鏃剁殑榛樿鍊?
     * @return 缁撴灉
     */
    public static String toStr(Object value, String defaultValue)
    {
        if (null == value)
        {
            return defaultValue;
        }
        if (value instanceof String)
        {
            return (String) value;
        }
        return value.toString();
    }

    /**
     * 杞崲涓哄瓧绗︿覆<br>
     * 濡傛灉缁欏畾鐨勫€间负<code>null</code>锛屾垨鑰呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?code>null</code><br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static String toStr(Object value)
    {
        return toStr(value, null);
    }

    /**
     * 杞崲涓哄瓧绗?br>
     * 濡傛灉缁欏畾鐨勫€间负null锛屾垨鑰呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @param defaultValue 杞崲閿欒鏃剁殑榛樿鍊?
     * @return 缁撴灉
     */
    public static Character toChar(Object value, Character defaultValue)
    {
        if (null == value)
        {
            return defaultValue;
        }
        if (value instanceof Character)
        {
            return (Character) value;
        }

        final String valueStr = toStr(value, null);
        return StringUtils.isEmpty(valueStr) ? defaultValue : valueStr.charAt(0);
    }

    /**
     * 杞崲涓哄瓧绗?br>
     * 濡傛灉缁欏畾鐨勫€间负<code>null</code>锛屾垨鑰呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?code>null</code><br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Character toChar(Object value)
    {
        return toChar(value, null);
    }

    /**
     * 杞崲涓篵yte<br>
     * 濡傛灉缁欏畾鐨勫€间负<code>null</code>锛屾垨鑰呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @param defaultValue 杞崲閿欒鏃剁殑榛樿鍊?
     * @return 缁撴灉
     */
    public static Byte toByte(Object value, Byte defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Byte)
        {
            return (Byte) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).byteValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Byte.parseByte(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 杞崲涓篵yte<br>
     * 濡傛灉缁欏畾鐨勫€间负<code>null</code>锛屾垨鑰呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?code>null</code><br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Byte toByte(Object value)
    {
        return toByte(value, null);
    }

    /**
     * 杞崲涓篠hort<br>
     * 濡傛灉缁欏畾鐨勫€间负<code>null</code>锛屾垨鑰呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @param defaultValue 杞崲閿欒鏃剁殑榛樿鍊?
     * @return 缁撴灉
     */
    public static Short toShort(Object value, Short defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Short)
        {
            return (Short) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).shortValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Short.parseShort(valueStr.trim());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 杞崲涓篠hort<br>
     * 濡傛灉缁欏畾鐨勫€间负<code>null</code>锛屾垨鑰呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?code>null</code><br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Short toShort(Object value)
    {
        return toShort(value, null);
    }

    /**
     * 杞崲涓篘umber<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @param defaultValue 杞崲閿欒鏃剁殑榛樿鍊?
     * @return 缁撴灉
     */
    public static Number toNumber(Object value, Number defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Number)
        {
            return (Number) value;
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return NumberFormat.getInstance().parse(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 杞崲涓篘umber<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?code>null</code><br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Number toNumber(Object value)
    {
        return toNumber(value, null);
    }

    /**
     * 杞崲涓篿nt<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @param defaultValue 杞崲閿欒鏃剁殑榛樿鍊?
     * @return 缁撴灉
     */
    public static Integer toInt(Object value, Integer defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Integer)
        {
            return (Integer) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).intValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Integer.parseInt(valueStr.trim());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 杞崲涓篿nt<br>
     * 濡傛灉缁欏畾鐨勫€间负<code>null</code>锛屾垨鑰呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?code>null</code><br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Integer toInt(Object value)
    {
        return toInt(value, null);
    }

    /**
     * 杞崲涓篒nteger鏁扮粍<br>
     *
     * @param str 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Integer[] toIntArray(String str)
    {
        return toIntArray(",", str);
    }

    /**
     * 杞崲涓篖ong鏁扮粍<br>
     *
     * @param str 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Long[] toLongArray(String str)
    {
        return toLongArray(",", str);
    }

    /**
     * 杞崲涓篒nteger鏁扮粍<br>
     *
     * @param split 鍒嗛殧绗?
     * @param split 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Integer[] toIntArray(String split, String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return new Integer[] {};
        }
        String[] arr = str.split(split);
        final Integer[] ints = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            final Integer v = toInt(arr[i], 0);
            ints[i] = v;
        }
        return ints;
    }

    /**
     * 杞崲涓篖ong鏁扮粍<br>
     *
     * @param split 鍒嗛殧绗?
     * @param str 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Long[] toLongArray(String split, String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return new Long[] {};
        }
        String[] arr = str.split(split);
        final Long[] longs = new Long[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            final Long v = toLong(arr[i], null);
            longs[i] = v;
        }
        return longs;
    }

    /**
     * 杞崲涓篠tring鏁扮粍<br>
     *
     * @param str 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static String[] toStrArray(String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return new String[] {};
        }
        return toStrArray(",", str);
    }

    /**
     * 杞崲涓篠tring鏁扮粍<br>
     *
     * @param split 鍒嗛殧绗?
     * @param split 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static String[] toStrArray(String split, String str)
    {
        return str.split(split);
    }

    /**
     * 杞崲涓簂ong<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @param defaultValue 杞崲閿欒鏃剁殑榛樿鍊?
     * @return 缁撴灉
     */
    public static Long toLong(Object value, Long defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Long)
        {
            return (Long) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            // 鏀寔绉戝璁℃暟娉?
            return new BigDecimal(valueStr.trim()).longValue();
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 杞崲涓簂ong<br>
     * 濡傛灉缁欏畾鐨勫€间负<code>null</code>锛屾垨鑰呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?code>null</code><br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Long toLong(Object value)
    {
        return toLong(value, null);
    }

    /**
     * 杞崲涓篸ouble<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @param defaultValue 杞崲閿欒鏃剁殑榛樿鍊?
     * @return 缁撴灉
     */
    public static Double toDouble(Object value, Double defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Double)
        {
            return (Double) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).doubleValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            // 鏀寔绉戝璁℃暟娉?
            return new BigDecimal(valueStr.trim()).doubleValue();
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 杞崲涓篸ouble<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?code>null</code><br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Double toDouble(Object value)
    {
        return toDouble(value, null);
    }

    /**
     * 杞崲涓篎loat<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @param defaultValue 杞崲閿欒鏃剁殑榛樿鍊?
     * @return 缁撴灉
     */
    public static Float toFloat(Object value, Float defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Float)
        {
            return (Float) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).floatValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Float.parseFloat(valueStr.trim());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 杞崲涓篎loat<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?code>null</code><br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Float toFloat(Object value)
    {
        return toFloat(value, null);
    }

    /**
     * 杞崲涓篵oolean<br>
     * String鏀寔鐨勫€间负锛歵rue銆乫alse銆亂es銆乷k銆乶o銆?銆?銆佹槸銆佸惁, 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @param defaultValue 杞崲閿欒鏃剁殑榛樿鍊?
     * @return 缁撴灉
     */
    public static Boolean toBool(Object value, Boolean defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Boolean)
        {
            return (Boolean) value;
        }
        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        valueStr = valueStr.trim().toLowerCase();
        switch (valueStr)
        {
            case "true":
            case "yes":
            case "ok":
            case "1":
            case "鏄?:
                return true;
            case "false":
            case "no":
            case "0":
            case "鍚?:
                return false;
            default:
                return defaultValue;
        }
    }

    /**
     * 杞崲涓篵oolean<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?code>null</code><br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static Boolean toBool(Object value)
    {
        return toBool(value, null);
    }

    /**
     * 杞崲涓篍num瀵硅薄<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     *
     * @param clazz Enum鐨凜lass
     * @param value 鍊?
     * @param defaultValue 榛樿鍊?
     * @return Enum
     */
    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value, E defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (clazz.isAssignableFrom(value.getClass()))
        {
            @SuppressWarnings("unchecked")
            E myE = (E) value;
            return myE;
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Enum.valueOf(clazz, valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 杞崲涓篍num瀵硅薄<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?code>null</code><br>
     *
     * @param clazz Enum鐨凜lass
     * @param value 鍊?
     * @return Enum
     */
    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value)
    {
        return toEnum(clazz, value, null);
    }

    /**
     * 杞崲涓築igInteger<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @param defaultValue 杞崲閿欒鏃剁殑榛樿鍊?
     * @return 缁撴灉
     */
    public static BigInteger toBigInteger(Object value, BigInteger defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof BigInteger)
        {
            return (BigInteger) value;
        }
        if (value instanceof Long)
        {
            return BigInteger.valueOf((Long) value);
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return new BigInteger(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 杞崲涓築igInteger<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?code>null</code><br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static BigInteger toBigInteger(Object value)
    {
        return toBigInteger(value, null);
    }

    /**
     * 杞崲涓築igDecimal<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @param defaultValue 杞崲閿欒鏃剁殑榛樿鍊?
     * @return 缁撴灉
     */
    public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof BigDecimal)
        {
            return (BigDecimal) value;
        }
        if (value instanceof Long)
        {
            return new BigDecimal((Long) value);
        }
        if (value instanceof Double)
        {
            return BigDecimal.valueOf((Double) value);
        }
        if (value instanceof Integer)
        {
            return new BigDecimal((Integer) value);
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return new BigDecimal(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 杞崲涓築igDecimal<br>
     * 濡傛灉缁欏畾鐨勫€间负绌猴紝鎴栬€呰浆鎹㈠け璐ワ紝杩斿洖榛樿鍊?br>
     * 杞崲澶辫触涓嶄細鎶ラ敊
     *
     * @param value 琚浆鎹㈢殑鍊?
     * @return 缁撴灉
     */
    public static BigDecimal toBigDecimal(Object value)
    {
        return toBigDecimal(value, null);
    }

    /**
     * 灏嗗璞¤浆涓哄瓧绗︿覆<br>
     * 1銆丅yte鏁扮粍鍜孊yteBuffer浼氳杞崲涓哄搴斿瓧绗︿覆鐨勬暟缁?2銆佸璞℃暟缁勪細璋冪敤Arrays.toString鏂规硶
     *
     * @param obj 瀵硅薄
     * @return 瀛楃涓?
     */
    public static String utf8Str(Object obj)
    {
        return str(obj, CharsetKit.CHARSET_UTF_8);
    }

    /**
     * 灏嗗璞¤浆涓哄瓧绗︿覆<br>
     * 1銆丅yte鏁扮粍鍜孊yteBuffer浼氳杞崲涓哄搴斿瓧绗︿覆鐨勬暟缁?2銆佸璞℃暟缁勪細璋冪敤Arrays.toString鏂规硶
     *
     * @param obj 瀵硅薄
     * @param charsetName 瀛楃闆?
     * @return 瀛楃涓?
     */
    public static String str(Object obj, String charsetName)
    {
        return str(obj, Charset.forName(charsetName));
    }

    /**
     * 灏嗗璞¤浆涓哄瓧绗︿覆<br>
     * 1銆丅yte鏁扮粍鍜孊yteBuffer浼氳杞崲涓哄搴斿瓧绗︿覆鐨勬暟缁?2銆佸璞℃暟缁勪細璋冪敤Arrays.toString鏂规硶
     *
     * @param obj 瀵硅薄
     * @param charset 瀛楃闆?
     * @return 瀛楃涓?
     */
    public static String str(Object obj, Charset charset)
    {
        if (null == obj)
        {
            return null;
        }

        if (obj instanceof String)
        {
            return (String) obj;
        }
        else if (obj instanceof byte[] || obj instanceof Byte[])
        {
            if (obj instanceof byte[])
            {
                return str((byte[]) obj, charset);
            }
            else
            {
                Byte[] bytes = (Byte[]) obj;
                int length = bytes.length;
                byte[] dest = new byte[length];
                for (int i = 0; i < length; i++)
                {
                    dest[i] = bytes[i];
                }
                return str(dest, charset);
            }
        }
        else if (obj instanceof ByteBuffer)
        {
            return str((ByteBuffer) obj, charset);
        }
        return obj.toString();
    }

    /**
     * 灏哹yte鏁扮粍杞负瀛楃涓?
     *
     * @param bytes byte鏁扮粍
     * @param charset 瀛楃闆?
     * @return 瀛楃涓?
     */
    public static String str(byte[] bytes, String charset)
    {
        return str(bytes, StringUtils.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 瑙ｇ爜瀛楄妭鐮?
     *
     * @param data 瀛楃涓?
     * @param charset 瀛楃闆嗭紝濡傛灉姝ゅ瓧娈典负绌猴紝鍒欒В鐮佺殑缁撴灉鍙栧喅浜庡钩鍙?
     * @return 瑙ｇ爜鍚庣殑瀛楃涓?
     */
    public static String str(byte[] data, Charset charset)
    {
        if (data == null)
        {
            return null;
        }

        if (null == charset)
        {
            return new String(data);
        }
        return new String(data, charset);
    }

    /**
     * 灏嗙紪鐮佺殑byteBuffer鏁版嵁杞崲涓哄瓧绗︿覆
     *
     * @param data 鏁版嵁
     * @param charset 瀛楃闆嗭紝濡傛灉涓虹┖浣跨敤褰撳墠绯荤粺瀛楃闆?
     * @return 瀛楃涓?
     */
    public static String str(ByteBuffer data, String charset)
    {
        if (data == null)
        {
            return null;
        }

        return str(data, Charset.forName(charset));
    }

    /**
     * 灏嗙紪鐮佺殑byteBuffer鏁版嵁杞崲涓哄瓧绗︿覆
     *
     * @param data 鏁版嵁
     * @param charset 瀛楃闆嗭紝濡傛灉涓虹┖浣跨敤褰撳墠绯荤粺瀛楃闆?
     * @return 瀛楃涓?
     */
    public static String str(ByteBuffer data, Charset charset)
    {
        if (null == charset)
        {
            charset = Charset.defaultCharset();
        }
        return charset.decode(data).toString();
    }

    // ----------------------------------------------------------------------- 鍏ㄨ鍗婅杞崲
    /**
     * 鍗婅杞叏瑙?
     *
     * @param input String.
     * @return 鍏ㄨ瀛楃涓?
     */
    public static String toSBC(String input)
    {
        return toSBC(input, null);
    }

    /**
     * 鍗婅杞叏瑙?
     *
     * @param input String
     * @param notConvertSet 涓嶆浛鎹㈢殑瀛楃闆嗗悎
     * @return 鍏ㄨ瀛楃涓?
     */
    public static String toSBC(String input, Set<Character> notConvertSet)
    {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++)
        {
            if (null != notConvertSet && notConvertSet.contains(c[i]))
            {
                // 璺宠繃涓嶆浛鎹㈢殑瀛楃
                continue;
            }

            if (c[i] == ' ')
            {
                c[i] = '\u3000';
            }
            else if (c[i] < '\177')
            {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 鍏ㄨ杞崐瑙?
     *
     * @param input String.
     * @return 鍗婅瀛楃涓?
     */
    public static String toDBC(String input)
    {
        return toDBC(input, null);
    }

    /**
     * 鏇挎崲鍏ㄨ涓哄崐瑙?
     *
     * @param text 鏂囨湰
     * @param notConvertSet 涓嶆浛鎹㈢殑瀛楃闆嗗悎
     * @return 鏇挎崲鍚庣殑瀛楃
     */
    public static String toDBC(String text, Set<Character> notConvertSet)
    {
        char[] c = text.toCharArray();
        for (int i = 0; i < c.length; i++)
        {
            if (null != notConvertSet && notConvertSet.contains(c[i]))
            {
                // 璺宠繃涓嶆浛鎹㈢殑瀛楃
                continue;
            }

            if (c[i] == '\u3000')
            {
                c[i] = ' ';
            }
            else if (c[i] > '\uFF00' && c[i] < '\uFF5F')
            {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 鏁板瓧閲戦澶у啓杞崲 鍏堝啓涓畬鏁寸殑鐒跺悗灏嗗闆舵嬀鏇挎崲鎴愰浂
     *
     * @param n 鏁板瓧
     * @return 涓枃澶у啓鏁板瓧
     */
    public static String digitUppercase(double n)
    {
        String[] fraction = { "瑙?, "鍒? };
        String[] digit = { "闆?, "澹?, "璐?, "鍙?, "鑲?, "浼?, "闄?, "鏌?, "鎹?, "鐜? };
        String[][] unit = { { "鍏?, "涓?, "浜? }, { "", "鎷?, "浣?, "浠? } };

        String head = n < 0 ? "璐? : "";
        n = Math.abs(n);

        String s = "";
        for (int i = 0; i < fraction.length; i++)
        {
            // 浼樺寲double璁＄畻绮惧害涓㈠け闂
            BigDecimal nNum = new BigDecimal(n);
            BigDecimal decimal = new BigDecimal(10);
            BigDecimal scale = nNum.multiply(decimal).setScale(2, RoundingMode.HALF_EVEN);
            double d = scale.doubleValue();
            s += (digit[(int) (Math.floor(d * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(闆?)+", "");
        }
        if (s.length() < 1)
        {
            s = "鏁?;
        }
        int integerPart = (int) Math.floor(n);

        for (int i = 0; i < unit[0].length && integerPart > 0; i++)
        {
            String p = "";
            for (int j = 0; j < unit[1].length && n > 0; j++)
            {
                p = digit[integerPart % 10] + unit[1][j] + p;
                integerPart = integerPart / 10;
            }
            s = p.replaceAll("(闆?)*闆?", "").replaceAll("^$", "闆?) + unit[0][i] + s;
        }
        return head + s.replaceAll("(闆?)*闆跺厓", "鍏?).replaceFirst("(闆?)+", "").replaceAll("(闆?)+", "闆?).replaceAll("^鏁?", "闆跺厓鏁?);
    }
}
