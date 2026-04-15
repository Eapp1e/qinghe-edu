package com.eapple.common.enums;

import java.util.function.Function;
import com.eapple.common.utils.DesensitizedUtil;

/**
 * 鑴辨晱绫诲瀷
 *
 * @author Eapp1e
 */
public enum DesensitizedType
{
    /**
     * 濮撳悕锛岀2浣嶆槦鍙锋浛鎹?
     */
    USERNAME(s -> s.replaceAll("(\\S)\\S(\\S*)", "$1*$2")),

    /**
     * 瀵嗙爜锛屽叏閮ㄥ瓧绗﹂兘鐢?浠ｆ浛
     */
    PASSWORD(DesensitizedUtil::password),

    /**
     * 韬唤璇侊紝涓棿10浣嶆槦鍙锋浛鎹?
     */
    ID_CARD(s -> s.replaceAll("(\\d{4})\\d{10}(\\d{3}[Xx]|\\d{4})", "$1** **** ****$2")),

    /**
     * 鎵嬫満鍙凤紝涓棿4浣嶆槦鍙锋浛鎹?
     */
    PHONE(s -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),

    /**
     * 鐢靛瓙閭锛屼粎鏄剧ず绗竴涓瓧姣嶅拰@鍚庨潰鐨勫湴鍧€鏄剧ず锛屽叾浠栨槦鍙锋浛鎹?
     */
    EMAIL(s -> s.replaceAll("(^.)[^@]*(@.*$)", "$1****$2")),

    /**
     * 閾惰鍗″彿锛屼繚鐣欐渶鍚?浣嶏紝鍏朵粬鏄熷彿鏇挎崲
     */
    BANK_CARD(s -> s.replaceAll("\\d{15}(\\d{3})", "**** **** **** **** $1")),

    /**
     * 杞︾墝鍙风爜锛屽寘鍚櫘閫氳溅杈嗐€佹柊鑳芥簮杞﹁締
     */
    CAR_LICENSE(DesensitizedUtil::carLicense);

    private final Function<String, String> desensitizer;

    DesensitizedType(Function<String, String> desensitizer)
    {
        this.desensitizer = desensitizer;
    }

    public Function<String, String> desensitizer()
    {
        return desensitizer;
    }
}
