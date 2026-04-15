package com.eapple.common.utils;

/**
 * 鑴辨晱宸ュ叿绫?
 *
 * @author Eapp1e
 */
public class DesensitizedUtil
{
    /**
     * 瀵嗙爜鐨勫叏閮ㄥ瓧绗﹂兘鐢?浠ｆ浛锛屾瘮濡傦細******
     *
     * @param password 瀵嗙爜
     * @return 鑴辨晱鍚庣殑瀵嗙爜
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
     * 杞︾墝涓棿鐢?浠ｆ浛锛屽鏋滄槸閿欒鐨勮溅鐗岋紝涓嶅鐞?
     *
     * @param carLicense 瀹屾暣鐨勮溅鐗屽彿
     * @return 鑴辨晱鍚庣殑杞︾墝
     */
    public static String carLicense(String carLicense)
    {
        if (StringUtils.isBlank(carLicense))
        {
            return StringUtils.EMPTY;
        }
        // 鏅€氳溅鐗?
        if (carLicense.length() == 7)
        {
            carLicense = StringUtils.hide(carLicense, 3, 6);
        }
        else if (carLicense.length() == 8)
        {
            // 鏂拌兘婧愯溅鐗?
            carLicense = StringUtils.hide(carLicense, 3, 7);
        }
        return carLicense;
    }
}
