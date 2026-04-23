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
 * OpenAPI configuration.
 *
 * @author EAPPLE
 */
@Configuration
public class SwaggerConfig
{
    /** Platform base configuration. */
    @Autowired
    private PlatformConfig platformConfig;

    /**
     * Creates the OpenAPI document model.
     */
    @Bean
    public OpenAPI customOpenApi()
    {
        return new OpenAPI().components(new Components()
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
     * Builds API document metadata.
     */
    public Info getApiInfo()
    {
        return new Info()
            .title("QINGHE After-school Service Platform API")
            .description("API documentation for QINGHE After-school Service Platform, covering role capabilities and education service workflows.")
            .contact(new Contact().name("EAPPLE").url("https://github.com/Eapp1e"))
            .version("v" + platformConfig.getVersion());
    }
}
