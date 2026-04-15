package com.eapple.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 鏁版嵁鏉冮檺杩囨护娉ㄨВ
 * 
 * @author Eapp1e
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope
{
    /**
     * 鐢ㄦ埛琛ㄧ殑鍒悕
     */
    public String userAlias() default "";

    /**
     * 閮ㄩ棬琛ㄧ殑鍒悕
     */
    public String deptAlias() default "";

    /**
     * 鐢ㄦ埛瀛楁鍚?
     */
    public String userField() default "user_id";

    /**
     * 閮ㄩ棬瀛楁鍚?
     */
    public String deptField() default "dept_id";

    /**
     * 鏉冮檺瀛楃锛堢敤浜庡涓鑹插尮閰嶇鍚堣姹傜殑鏉冮檺锛夐粯璁ゆ牴鎹潈闄愭敞瑙ss鑾峰彇锛屽涓潈闄愮敤閫楀彿鍒嗛殧寮€鏉?
     */
    public String permission() default "";
}
