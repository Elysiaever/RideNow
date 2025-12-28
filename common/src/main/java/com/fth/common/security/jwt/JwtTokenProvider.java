package com.fth.common.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fth.common.core.constant.ExceptionConstant;
import com.fth.common.exception.BusinessException;
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

    public void validateToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
        } catch (JWTVerificationException e) {
            // 补充错误码（例如 401 表示认证失败），并携带异常信息
            throw new BusinessException(401, ExceptionConstant.TOKEN_INVALID);

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
            // 补充错误码（与上面保持一致，均为 Token 相关错误）
            throw new BusinessException(401, ExceptionConstant.TOKEN_INVALID);

        }
    }
    /**
     * 获取Token过期时间（秒）
     */
    public long getExpirationSeconds() {
        return expireMinutes * 60;
    }
}