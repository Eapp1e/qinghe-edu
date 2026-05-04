package com.qinghe.common.utils;

/**
 * 脱敏工具类。
 *
 * @author Eapp1e
 */
public class DesensitizedUtil
{
    /**
     * 密码脱敏，全部替换为星号。
     *
     * @param password 原始密码
     * @return 脱敏后的密码
     */
    public static String password(String password)
    {
        if (StringUtils.isBlank(password))
        {
            return StringUtils.EMPTY;
        }
        return StringUtils.repeat('*', password.length());
    }

    /**
     * 车牌号脱敏。
     * 常规车牌保留前三位，新能源车牌适当多保留一位。
     *
     * @param carLicense 原始车牌号
     * @return 脱敏后的车牌号
     */
    public static String carLicense(String carLicense)
    {
        if (StringUtils.isBlank(carLicense))
        {
            return StringUtils.EMPTY;
        }
        if (carLicense.length() == 7)
        {
            carLicense = StringUtils.hide(carLicense, 3, 6);
        }
        else if (carLicense.length() == 8)
        {
            carLicense = StringUtils.hide(carLicense, 3, 7);
        }
        return carLicense;
    }
}
