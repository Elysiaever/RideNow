package com.fth.user.config;

import com.fth.user.security.JwtUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // 👉 调试：输出 Authorization 头和 Token（注意格式）
        System.out.println("Authorization Header = " + header);

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            // 👉 调试：输出 Token
            System.out.println("Extracted JWT Token = " + token);

            if (jwtUtils.validateToken(token)) {

                // 👉 调试：输出 Token 校验成功
                System.out.println("JWT Validate Success!");

                String username = jwtUtils.getUsernameFromToken(token);
                System.out.println("Username from Token = " + username);

                List<String> roles = jwtUtils.getRolesFromToken(token);
                System.out.println("Roles from Token = " + roles);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                // 👉 调试： Token 校验失败
                System.out.println("JWT Validate Failed!");
            }
        }

        filterChain.doFilter(request, response);
    }
}