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
 * Swagger2鐨勬帴鍙ｉ厤缃?
 * 
 * @author Eapp1e
 */
@Configuration
public class SwaggerConfig
{
    /** 绯荤粺鍩虹閰嶇疆 */
    @Autowired
    private PlatformConfig ruoyiConfig;
    
    /**
     * 鑷畾涔夌殑 OpenAPI 瀵硅薄
     */
    @Bean
    public OpenAPI customOpenApi()
    {
        return new OpenAPI().components(new Components()
            // 璁剧疆璁よ瘉鐨勮姹傚ご
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
     * 娣诲姞鎽樿淇℃伅
     */
    public Info getApiInfo()
    {
        return new Info()
            // 璁剧疆鏍囬
            .title("鏍囬锛氳嫢渚濈鐞嗙郴缁焈鎺ュ彛鏂囨。")
            // 鎻忚堪
            .description("鎻忚堪锛氱敤浜庣鐞嗛泦鍥㈡棗涓嬪叕鍙哥殑浜哄憳淇℃伅,鍏蜂綋鍖呮嫭XXX,XXX妯″潡...")
            // 浣滆€呬俊鎭?
            .contact(new Contact().name(ruoyiConfig.getName()))
            // 鐗堟湰
            .version("鐗堟湰鍙?" + ruoyiConfig.getVersion());
    }
}

