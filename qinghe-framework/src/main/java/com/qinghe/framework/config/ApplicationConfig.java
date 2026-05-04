package com.qinghe.framework.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 程序注解配置。
 *
 * @author Eapp1e
 */
@Configuration
// 表示通过 AOP 框架暴露代理对象，AopContext 可直接访问当前代理。
@EnableAspectJAutoProxy(exposeProxy = true)
// 扫描 Mapper 接口所在包。
@MapperScan("com.qinghe.**.mapper")
public class ApplicationConfig
{
}
