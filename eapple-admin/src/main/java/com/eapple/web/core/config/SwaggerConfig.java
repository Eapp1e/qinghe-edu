package com.eapple.web.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.eapple.common.config.PlatformConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * OpenAPI 文档配置
 *
 * @author Eapp1e
 */
@Configuration
public class SwaggerConfig
{
    /** 平台基础配置 */
    @Autowired
    private PlatformConfig platformConfig;

    /**
     * 自定义 OpenAPI 对象
     */
    @Bean
    public OpenAPI customOpenApi()
    {
        return new OpenAPI().components(new Components()
            // 设置认证请求头
            .addSecuritySchemes("apikey", securityScheme()))
            .addSecurityItem(new SecurityRequirement().addList("apikey"))
            .info(getApiInfo());
    }

    @Bean
    public SecurityScheme securityScheme()
    {
        return new SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .name("Authorization")
            .in(SecurityScheme.In.HEADER)
            .scheme("Bearer");
    }

    /**
     * 构建接口文档基础信息
     */
    public Info getApiInfo()
    {
        return new Info()
            // 文档标题
            .title("青禾智学课后服务平台接口文档")
            // 文档描述
            .description("用于展示青禾智学课后服务平台的后端接口、角色能力与业务数据访问说明。")
            // 联系信息
            .contact(new Contact().name(platformConfig.getName()))
            // 版本号
            .version("v" + platformConfig.getVersion());
    }
}
