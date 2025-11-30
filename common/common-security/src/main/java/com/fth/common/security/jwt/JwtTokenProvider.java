package com.fth.common.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fth.common.security.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final Algorithm algorithm;
    private final long expireMinutes;

    @Autowired
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,  // 强制从配置读取密钥（无默认值）
            @Value("${jwt.expire-minutes:60}") long expireMinutes) {  // 默认过期1小时，更合理
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.expireMinutes = expireMinutes;
    }

    // ================================= 新增：生成 Token 相关方法 =================================
    /**
     * 基于 Spring Security UserDetails 生成 Token（推荐，自动关联用户名和角色）
     * 适配 Spring Security 认证体系，无需手动传角色
     */
    public String generateToken(UserDetails userDetails) {
        // 从 UserDetails 中提取角色（格式：ROLE_XXX）
        List<String> roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        return JWT.create()
                .withSubject(userDetails.getUsername())  // 用户名作为 Token 主题（与解析逻辑对应）
                .withClaim("roles", roles)  // 存储角色列表
                .withExpiresAt(new Date(System.currentTimeMillis() + expireMinutes * 60 * 1000))  // 过期时间
                .withIssuedAt(new Date())  // 签发时间（可选，增强安全性）
                .sign(algorithm);  // 用密钥签名
    }

    /**
     * 手动传入用户名和角色生成 Token（适配非 Spring Security 场景，如第三方登录）
     */
    public String generateToken(String username, List<String> roles) {
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", roles)
                .withExpiresAt(new Date(System.currentTimeMillis() + expireMinutes * 60 * 1000))
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    // ================================= 优化：解析 Token 逻辑（避免重复验证） =================================
    /**
     * 解析 Token（内部已包含有效性验证，无效抛异常）
     */
    private DecodedJWT decodeToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
            // 额外校验过期时间（JWT.verify() 已校验，此处冗余增强，可选删除）
            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new InvalidTokenException("Token已过期");
            }
            return decodedJWT;
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException("无效的Token: " + e.getMessage());
        }
    }

    /**
     * 从令牌解析用户名（复用 decodeToken，避免重复验证）
     */
    public String getUsernameFromToken(String token) {
        return decodeToken(token).getSubject();  // 与 generateToken 的 withSubject 对应
    }

    /**
     * 从令牌解析角色列表（复用 decodeToken，避免重复验证）
     */
    public List<String> getRolesFromToken(String token) {
        return decodeToken(token).getClaim("roles").asList(String.class);
    }

    // ================================= 原有方法保留（优化复用逻辑） =================================
    /**
     * 验证令牌有效性（无效则抛异常）
     */
    public void validateToken(String token) {
        decodeToken(token);  // 直接复用解析逻辑，无需重复写验证代码
    }

    /**
     * 获取令牌过期时间（秒）
     */
    public long getExpirationSeconds() {
        return expireMinutes * 60;
    }

    /**
     * 暴露 Algorithm（供外部扩展，如自定义 Token 生成逻辑，可选）
     */
    public Algorithm getAlgorithm() {
        return algorithm;
    }
}