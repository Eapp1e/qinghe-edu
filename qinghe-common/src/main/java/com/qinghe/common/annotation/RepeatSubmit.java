package com.qinghe.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防止重复提交注解。
 *
 * @author Eapp1e
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit
{
    /**
     * 间隔时间（毫秒），小于此时间视为重复提交。
     */
    public int interval() default 5000;

    /**
     * 提示消息。
     */
    public String message() default "不允许重复提交，请稍后再试";
}
