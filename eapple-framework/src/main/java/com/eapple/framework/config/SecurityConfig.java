package com.eapple.framework.config;

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
import com.eapple.framework.config.properties.PermitAllUrlProperties;
import com.eapple.framework.security.filter.JwtAuthenticationTokenFilter;
import com.eapple.framework.security.handle.AuthenticationEntryPointImpl;
import com.eapple.framework.security.handle.LogoutSuccessHandlerImpl;

/**
 * spring security閰嶇疆
 * 
 * @author Eapp1e
 */
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
public class SecurityConfig
{
    /**
     * 璁よ瘉澶辫触澶勭悊绫?
     */
    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /**
     * 閫€鍑哄鐞嗙被
     */
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    /**
     * token璁よ瘉杩囨护鍣?
     */
    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;
    
    /**
     * 璺ㄥ煙杩囨护鍣?
     */
    @Autowired
    private CorsFilter corsFilter;

    /**
     * 鍏佽鍖垮悕璁块棶鐨勫湴鍧€
     */
    @Autowired
    private PermitAllUrlProperties permitAllUrl;

	/**
	 * 韬唤楠岃瘉瀹炵幇
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception 
	{
		return authenticationConfiguration.getAuthenticationManager();
	}

    /**
     * anyRequest          |   鍖归厤鎵€鏈夎姹傝矾寰?
     * access              |   SpringEl琛ㄨ揪寮忕粨鏋滀负true鏃跺彲浠ヨ闂?
     * anonymous           |   鍖垮悕鍙互璁块棶
     * denyAll             |   鐢ㄦ埛涓嶈兘璁块棶
     * fullyAuthenticated  |   鐢ㄦ埛瀹屽叏璁よ瘉鍙互璁块棶锛堥潪remember-me涓嬭嚜鍔ㄧ櫥褰曪級
     * hasAnyAuthority     |   濡傛灉鏈夊弬鏁帮紝鍙傛暟琛ㄧず鏉冮檺锛屽垯鍏朵腑浠讳綍涓€涓潈闄愬彲浠ヨ闂?
     * hasAnyRole          |   濡傛灉鏈夊弬鏁帮紝鍙傛暟琛ㄧず瑙掕壊锛屽垯鍏朵腑浠讳綍涓€涓鑹插彲浠ヨ闂?
     * hasAuthority        |   濡傛灉鏈夊弬鏁帮紝鍙傛暟琛ㄧず鏉冮檺锛屽垯鍏舵潈闄愬彲浠ヨ闂?
     * hasIpAddress        |   濡傛灉鏈夊弬鏁帮紝鍙傛暟琛ㄧずIP鍦板潃锛屽鏋滅敤鎴稩P鍜屽弬鏁板尮閰嶏紝鍒欏彲浠ヨ闂?
     * hasRole             |   濡傛灉鏈夊弬鏁帮紝鍙傛暟琛ㄧず瑙掕壊锛屽垯鍏惰鑹插彲浠ヨ闂?
     * permitAll           |   鐢ㄦ埛鍙互浠绘剰璁块棶
     * rememberMe          |   鍏佽閫氳繃remember-me鐧诲綍鐨勭敤鎴疯闂?
     * authenticated       |   鐢ㄦ埛鐧诲綍鍚庡彲璁块棶
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception
    {
        return httpSecurity
            // CSRF绂佺敤锛屽洜涓轰笉浣跨敤session
            .csrf(csrf -> csrf.disable())
            // 绂佺敤HTTP鍝嶅簲鏍囧ご
            .headers((headersCustomizer) -> {
                headersCustomizer.cacheControl(cache -> cache.disable()).frameOptions(options -> options.sameOrigin());
            })
            // 璁よ瘉澶辫触澶勭悊绫?
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            // 鍩轰簬token锛屾墍浠ヤ笉闇€瑕乻ession
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 娉ㄨВ鏍囪鍏佽鍖垮悕璁块棶鐨剈rl
            .authorizeHttpRequests((requests) -> {
                permitAllUrl.getUrls().forEach(url -> requests.requestMatchers(url).permitAll());
                // 瀵逛簬鐧诲綍login 娉ㄥ唽register 楠岃瘉鐮乧aptchaImage 鍏佽鍖垮悕璁块棶
                requests.requestMatchers("/login", "/register", "/captchaImage").permitAll()
                    // 闈欐€佽祫婧愶紝鍙尶鍚嶈闂?
                    .requestMatchers(HttpMethod.GET, "/", "/*.html", "/**.html", "/**.css", "/**.js", "/profile/**").permitAll()
                    .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**", "/druid/**").permitAll()
                    // 闄や笂闈㈠鐨勬墍鏈夎姹傚叏閮ㄩ渶瑕侀壌鏉冭璇?
                    .anyRequest().authenticated();
            })
            // 娣诲姞Logout filter
            .logout(logout -> logout.logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler))
            // 娣诲姞JWT filter
            .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
            // 娣诲姞CORS filter
            .addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class)
            .addFilterBefore(corsFilter, LogoutFilter.class)
            .build();
    }

    /**
     * 寮烘暎鍒楀搱甯屽姞瀵嗗疄鐜?
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
