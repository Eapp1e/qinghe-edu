package com.qinghe.common.enums;

import java.util.function.Function;
import com.qinghe.common.utils.DesensitizedUtil;

/**
 * 脱敏类型。
 *
 * @author Eapp1e
 */
public enum DesensitizedType
{
    /**
     * 用户名，从第 2 位开始脱敏。
     */
    USERNAME(s -> s.replaceAll("(\\S)\\S(\\S*)", "$1*$2")),

    /**
     * 密码，全部字符使用 * 替换。
     */
    PASSWORD(DesensitizedUtil::password),

    /**
     * 身份证号，中间 10 位脱敏。
     */
    ID_CARD(s -> s.replaceAll("(\\d{4})\\d{10}(\\d{3}[Xx]|\\d{4})", "$1** **** ****$2")),

    /**
     * 手机号，中间 4 位脱敏。
     */
    PHONE(s -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),

    /**
     * 邮箱，仅保留首字符和域名。
     */
    EMAIL(s -> s.replaceAll("(^.)[^@]*(@.*$)", "$1****$2")),

    /**
     * 银行卡号，仅保留后 3 位。
     */
    BANK_CARD(s -> s.replaceAll("\\d{15}(\\d{3})", "**** **** **** **** $1")),

    /**
     * 车牌号，按规则进行脱敏。
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
