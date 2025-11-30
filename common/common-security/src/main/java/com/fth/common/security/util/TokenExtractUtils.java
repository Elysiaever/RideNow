package com.fth.common.security.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractUtils {

    /**
     * 从请求头提取Bearer Token
     */
    public String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7); // 去除"Bearer "前缀
        }
        return null;
    }

    /**
     * 从请求头字符串提取Token（适用于网关等非Servlet环境）
     */
    public String extractTokenFromHeaderString(String header) {
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}