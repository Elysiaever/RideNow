package com.fth.common.security.filter;

import com.fth.common.security.jwt.JwtTokenProvider;
import com.fth.common.security.util.TokenExtractUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // 公开接口列表（无需验证Token）
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/user/login",
            "/api/user/register",
            "/actuator/**"
    );

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenExtractUtils tokenExtractUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        log.info("===== JwtAuthenticationFilter 执行 =====");
        log.info("请求路径：{}", requestUri);

        // 公开接口直接放行
        if (isPublicEndpoint(requestUri)) {
            log.info("公开接口，跳过Token验证：{}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        // 非公开接口：仅验证Token有效性和提取用户名
        try {
            String token = tokenExtractUtils.extractTokenFromHeader(request);
            if (token == null) {
                log.warn("未携带Token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请携带Token");
                return;
            }

            // 仅验证Token签名和过期时间（不查数据库、不处理权限）
            jwtTokenProvider.validateToken(token);
            String username = jwtTokenProvider.getUsernameFromToken(token);
            log.info("Token验证通过，用户名：{}", username);

            // 构建最简单的认证上下文（仅用户名，无权限）
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.error("Token验证失败：{}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }

        // 继续执行后续逻辑
        filterChain.doFilter(request, response);
    }

    /**
     * 判断是否为公开接口（支持通配符）
     */
    private boolean isPublicEndpoint(String requestUri) {
        for (String publicEndpoint : PUBLIC_ENDPOINTS) {
            if (publicEndpoint.endsWith("/**")) {
                String prefix = publicEndpoint.substring(0, publicEndpoint.length() - 3);
                if (requestUri.startsWith(prefix)) {
                    return true;
                }
            } else if (publicEndpoint.equals(requestUri)) {
                return true;
            }
        }
        return false;
    }
}