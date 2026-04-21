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
 * Swagger2閻ㄥ嫭甯撮崣锝夊帳缂?
 * 
 * @author Eapp1e
 */
@Configuration
public class SwaggerConfig
{
    /** 缁崵绮洪崺铏诡攨闁板秶鐤?*/
    @Autowired
    private PlatformConfig platformConfig;
    
    /**
     * 閼奉亜鐣炬稊澶屾畱 OpenAPI 鐎电钖?
     */
    @Bean
    public OpenAPI customOpenApi()
    {
        return new OpenAPI().components(new Components()
            // 鐠佸墽鐤嗙拋銈堢槈閻ㄥ嫯顕Ч鍌氥仈
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
     * 濞ｈ濮為幗妯款洣娣団剝浼?
     */
    public Info getApiInfo()
    {
        return new Info()
            // 鐠佸墽鐤嗛弽鍥暯
            .title("閺嶅洭顣介敍姘冲娓氭繄顓搁悶鍡欓兇缂佺剤閹恒儱褰涢弬鍥ㄣ€?)
            // 閹诲繗鍫?
            .description("閹诲繗鍫敍姘辨暏娴滃海顓搁悶鍡涙肠閸ャ垺妫楁稉瀣彆閸欏摜娈戞禍鍝勬喅娣団剝浼?閸忚渹缍嬮崠鍛XXX,XXX濡€虫健...")
            // 娴ｆ粏鈧懍淇婇幁?
            .contact(new Contact().name(platformConfig.getName()))
            // 閻楀牊婀?
            .version("閻楀牊婀伴崣?" + platformConfig.getVersion());
    }
}



