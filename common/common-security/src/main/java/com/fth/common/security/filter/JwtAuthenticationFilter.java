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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // 1. 定义需要跳过 Token 验证的公开接口（与 SecurityConfig 的 permitAll 保持一致）
    // 支持精确匹配和通配符（/** 匹配子路径）
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/user/login",       // 登录接口
            "/api/user/register",    // 注册接口
            "/actuator/**"           // 监控接口（可选）
    );

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenExtractUtils tokenExtractUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        log.info("===== JwtAuthenticationFilter 开始执行 =====");
        log.info("当前请求路径：{}，请求方法：{}", requestUri, requestMethod);

        // 2. 关键逻辑：判断当前请求是否为公开接口，若是则直接放行
        if (isPublicEndpoint(requestUri)) {
            log.info("当前请求为公开接口，跳过 Token 验证，直接放行：{}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 非公开接口：执行 Token 验证逻辑（原有逻辑不变）
        try {
            // 提取Token
            String token = tokenExtractUtils.extractTokenFromHeader(request);
            log.info("从请求头提取到的Token：{}", token == null ? "null（未携带Token）" : token);

            // 验证Token并设置认证信息
            if (token != null) {
                log.info("开始验证Token有效性...");
                jwtTokenProvider.validateToken(token); // 验证失败会抛异常
                log.info("Token验证通过！");

                String username = jwtTokenProvider.getUsernameFromToken(token);
                List<String> roles = jwtTokenProvider.getRolesFromToken(token);
                log.info("从Token中解析到：用户名={}, 角色={}", username, roles);

                // 构建权限列表（Spring Security角色需加ROLE_前缀）
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
                log.info("构建的权限列表：{}", authorities);

                // 设置认证上下文
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("认证上下文设置成功，当前认证用户：{}", username);
            } else {
                log.warn("未从请求头中提取到Token，跳过Token验证");
            }
        } catch (Exception e) {
            // 认证失败：清除上下文（避免残留无效认证信息）
            SecurityContextHolder.clearContext();
            log.error("Token验证失败！请求路径：{}，错误信息：{}", requestUri, e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token无效或已过期：" + e.getMessage());
            return;
        }

        // 继续执行过滤器链
        log.info("===== JwtAuthenticationFilter 执行完毕，继续后续过滤器 =====");
        filterChain.doFilter(request, response);
    }

    /**
     * 辅助方法：判断当前路径是否为公开接口（支持通配符 **）
     * @param requestUri 当前请求路径（如 /api/user/login）
     * @return true=公开接口（跳过验证），false=需验证
     */
    private boolean isPublicEndpoint(String requestUri) {
        for (String publicEndpoint : PUBLIC_ENDPOINTS) {
            // 处理通配符场景：如 /actuator/** 匹配 /actuator/health、/actuator/info 等
            if (publicEndpoint.endsWith("/**")) {
                String prefix = publicEndpoint.substring(0, publicEndpoint.length() - 3);
                if (requestUri.startsWith(prefix)) {
                    return true;
                }
            }
            // 处理精确匹配场景：如 /api/user/login 完全匹配
            else if (publicEndpoint.equals(requestUri)) {
                return true;
            }
        }
        return false;
    }
}