package com.qinghe.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;
import com.qinghe.framework.config.properties.PermitAllUrlProperties;
import com.qinghe.framework.security.filter.JwtAuthenticationTokenFilter;
import com.qinghe.framework.security.handle.AuthenticationEntryPointImpl;
import com.qinghe.framework.security.handle.LogoutSuccessHandlerImpl;

/**
 * Spring Security 配置。
 *
 * @author Eapp1e
 */
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
public class SecurityConfig
{
    /**
     * 认证失败处理器。
     */
    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /**
     * 退出成功处理器。
     */
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    /**
     * JWT 认证过滤器。
     */
    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    /**
     * CORS 过滤器。
     */
    @Autowired
    private CorsFilter corsFilter;

    /**
     * 允许匿名访问的地址配置。
     */
    @Autowired
    private PermitAllUrlProperties permitAllUrl;

    /**
     * 认证管理器。
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * access              | Spring EL 表达式结果为 true 时允许访问
     * anonymous           | 匿名用户可访问
     * denyAll             | 拒绝所有访问
     * fullyAuthenticated  | 完全认证后可访问，不包含 remember-me 自动登录
     * hasAnyAuthority     | 拥有任意一个指定权限即可访问
     * hasAnyRole          | 拥有任意一个指定角色即可访问
     * hasAuthority        | 拥有指定权限即可访问
     * hasIpAddress        | 请求 IP 符合配置时允许访问
     * hasRole             | 拥有指定角色即可访问
     * permitAll           | 允许任意访问
     * rememberMe          | 通过 remember-me 登录的用户可访问
     * authenticated       | 登录后可访问
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception
    {
        return httpSecurity
            // 禁用 CSRF，因为当前系统不依赖 session
            .csrf(csrf -> csrf.disable())
            // 允许同源 iframe，并关闭默认缓存控制头
            .headers((headersCustomizer) -> {
                headersCustomizer.cacheControl(cache -> cache.disable()).frameOptions(options -> options.sameOrigin());
            })
            // 认证失败处理
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            // 基于 token，无需创建 session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置请求授权规则
            .authorizeHttpRequests((requests) -> {
                permitAllUrl.getUrls().forEach(url -> requests.requestMatchers(url).permitAll());
                // 登录、注册、验证码接口允许匿名访问
                requests.requestMatchers("/login", "/register", "/captchaImage").permitAll()
                    // 静态资源允许匿名访问
                    .requestMatchers(HttpMethod.GET, "/", "/*.html", "/**.html", "/**.css", "/**.js", "/profile/**").permitAll()
                    .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**", "/druid/**").permitAll()
                    // 其余请求全部需要认证
                    .anyRequest().authenticated();
            })
            // 添加退出过滤器
            .logout(logout -> logout.logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler))
            // 添加 JWT 过滤器
            .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
            // 添加 CORS 过滤器
            .addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class)
            .addFilterBefore(corsFilter, LogoutFilter.class)
            .build();
    }

    /**
     * 强散列哈希加密实现。
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
