package com.qinghe.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限过滤注解。
 *
 * @author Eapp1e
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope
{
    /**
     * 用户表别名。
     */
    String userAlias() default "";

    /**
     * 部门表别名。
     */
    String deptAlias() default "";

    /**
     * 用户字段名。
     */
    String userField() default "user_id";

    /**
     * 部门字段名。
     */
    String deptField() default "dept_id";

    /**
     * 权限标识。
     * 用于多个角色匹配满足要求的权限时进行过滤，默认从权限上下文中获取。
     */
    String permission() default "";
}