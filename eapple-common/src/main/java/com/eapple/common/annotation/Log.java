package com.eapple.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.eapple.common.enums.BusinessType;
import com.eapple.common.enums.OperatorType;

/**
 * 鑷畾涔夋搷浣滄棩蹇楄褰曟敞瑙?
 * 
 * @author Eapp1e
 *
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log
{
    /**
     * 妯″潡
     */
    public String title() default "";

    /**
     * 鍔熻兘
     */
    public BusinessType businessType() default BusinessType.OTHER;

    /**
     * 鎿嶄綔浜虹被鍒?
     */
    public OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 鏄惁淇濆瓨璇锋眰鐨勫弬鏁?
     */
    public boolean isSaveRequestData() default true;

    /**
     * 鏄惁淇濆瓨鍝嶅簲鐨勫弬鏁?
     */
    public boolean isSaveResponseData() default true;

    /**
     * 鎺掗櫎鎸囧畾鐨勮姹傚弬鏁?
     */
    public String[] excludeParamNames() default {};
}
