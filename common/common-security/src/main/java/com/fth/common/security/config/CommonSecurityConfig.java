package com.fth.common.security.config;

import com.fth.common.security.filter.JwtAuthenticationFilter;
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

    public CommonSecurityConfig(JwtTokenProvider jwtTokenProvider, TokenExtractUtils tokenExtractUtils) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenExtractUtils = tokenExtractUtils;
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
                // 配置URL访问规则（子模块可重写此方法扩展）
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/login", "/api/user/register").permitAll() // 公开接口
                        .requestMatchers("/actuator/**").permitAll() // 监控接口
                        .anyRequest().authenticated() // 其他接口需认证
                )
                // 添加JWT过滤器（在用户名密码过滤器之前执行）
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, tokenExtractUtils),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}