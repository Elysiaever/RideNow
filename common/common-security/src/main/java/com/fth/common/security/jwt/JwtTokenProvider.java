package com.fth.common.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fth.common.security.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Algorithm algorithm;
    private final long expireMinutes;

    @Autowired
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expire-minutes:60}") long expireMinutes) {
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.expireMinutes = expireMinutes;
    }

    /**
     * 仅通过用户名生成Token（无任何额外信息）
     */
    public String generateToken(String username) {
        return JWT.create()
                .withSubject(username) // 仅存储用户名
                .withExpiresAt(new Date(System.currentTimeMillis() + expireMinutes * 60 * 1000))
                .sign(algorithm);
    }

    /**
     * 验证Token有效性（仅校验签名和过期时间）
     */
    public void validateToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException("Token无效或已过期：" + e.getMessage());
        }
    }

    /**
     * 从Token解析用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException("Token解析失败：" + e.getMessage());
        }
    }

    /**
     * 获取Token过期时间（秒）
     */
    public long getExpirationSeconds() {
        return expireMinutes * 60;
    }
}