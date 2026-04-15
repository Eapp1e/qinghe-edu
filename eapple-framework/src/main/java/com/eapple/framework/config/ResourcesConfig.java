package com.eapple.framework.config;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.eapple.common.config.PlatformConfig;
import com.eapple.common.constant.Constants;
import com.eapple.framework.interceptor.RepeatSubmitInterceptor;

/**
 * 閫氱敤閰嶇疆
 * 
 * @author Eapp1e
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer
{
    @Autowired
    private RepeatSubmitInterceptor repeatSubmitInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        /** 鏈湴鏂囦欢涓婁紶璺緞 */
        registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
                .addResourceLocations("file:" + PlatformConfig.getProfile() + "/");

        /** swagger閰嶇疆 */
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());
    }

    /**
     * 鑷畾涔夋嫤鎴鍒?
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
    }

    /**
     * 璺ㄥ煙閰嶇疆
     */
    @Bean
    public CorsFilter corsFilter()
    {
        CorsConfiguration config = new CorsConfiguration();
        // 璁剧疆璁块棶婧愬湴鍧€
        config.addAllowedOriginPattern("*");
        // 璁剧疆璁块棶婧愯姹傚ご
        config.addAllowedHeader("*");
        // 璁剧疆璁块棶婧愯姹傛柟娉?
        config.addAllowedMethod("*");
        // 鏈夋晥鏈?1800绉?
        config.setMaxAge(1800L);
        // 娣诲姞鏄犲皠璺緞锛屾嫤鎴竴鍒囪姹?
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        // 杩斿洖鏂扮殑CorsFilter
        return new CorsFilter(source);
    }
}
