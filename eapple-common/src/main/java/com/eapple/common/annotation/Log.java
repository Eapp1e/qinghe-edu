package com.eapple.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.eapple.common.enums.BusinessType;
import com.eapple.common.enums.OperatorType;

/**
 * 操作日志注解。
 *
 * @author Eapp1e
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log
{
    /**
     * 模块标题。
     */
    public String title() default "";

    /**
     * 业务类型。
     */
    public BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人类别。
     */
    public OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求参数。
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存响应数据。
     */
    public boolean isSaveResponseData() default true;

    /**
     * 需要排除的请求参数名。
     */
    public String[] excludeParamNames() default {};
}
