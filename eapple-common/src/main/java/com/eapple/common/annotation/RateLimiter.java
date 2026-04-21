package com.eapple.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.eapple.common.constant.CacheConstants;
import com.eapple.common.enums.LimitType;

/**
 * 闄愭祦娉ㄨВ
 * 
 * @author Eapp1e
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter
{
    /**
     * 闄愭祦key
     */
    public String key() default CacheConstants.RATE_LIMIT_KEY;

    /**
     * 闄愭祦鏃堕棿,鍗曚綅绉?
     */
    public int time() default 60;

    /**
     * 闄愭祦娆℃暟
     */
    public int count() default 100;

    /**
     * 闄愭祦绫诲瀷
     */
    public LimitType limitType() default LimitType.DEFAULT;
}
