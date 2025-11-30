package com.fth.common.security.config;

import com.fth.common.security.jwt.JwtTokenProvider;
import com.fth.common.security.util.TokenExtractUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtAutoConfiguration {

    // 自动装配JWT工具类
    @Bean
    @ConditionalOnMissingBean
    public JwtTokenProvider jwtTokenProvider() {
        // 注意：实际运行时会通过@Value从业务服务的配置文件注入参数
        return new JwtTokenProvider("", 60);
    }

    // 自动装配Token提取工具
    @Bean
    @ConditionalOnMissingBean
    public TokenExtractUtils tokenExtractUtils() {
        return new TokenExtractUtils();
    }
}