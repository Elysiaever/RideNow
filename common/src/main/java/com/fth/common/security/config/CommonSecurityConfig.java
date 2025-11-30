package com.fth.common.security.config;

import com.fth.common.security.filter.JwtAuthenticationFilter;
import com.fth.common.security.handler.CustomAccessDeniedHandler;
import com.fth.common.security.handler.CustomAuthenticationEntryPoint;
import com.fth.common.security.jwt.JwtTokenProvider;
import com.fth.common.security.util.TokenExtractUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class CommonSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenExtractUtils tokenExtractUtils;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public CommonSecurityConfig(JwtTokenProvider jwtTokenProvider,
                                TokenExtractUtils tokenExtractUtils,
                                CustomAuthenticationEntryPoint authenticationEntryPoint,
                                CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenExtractUtils = tokenExtractUtils;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    /**
     * 密码加密器（BCrypt算法）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器（用于手动触发认证）
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 配置安全过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭CSRF（适用于无状态API）
                .csrf(csrf -> csrf.disable())
                // 禁用Session（JWT是无状态的）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 配置URL访问规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/login", "/api/user/register").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 自定义异常处理
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint) // 未登录或Token无效
                        .accessDeniedHandler(accessDeniedHandler)             // 权限不足
                )
                // 添加JWT过滤器（在用户名密码过滤器之前执行）
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, tokenExtractUtils),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
