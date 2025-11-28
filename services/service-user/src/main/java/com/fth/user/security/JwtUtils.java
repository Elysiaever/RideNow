package com.fth.user.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    private final Algorithm algorithm;
    private final long expireMinutes;

    public JwtUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expire-minutes}") long expireMinutes) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expireMinutes = expireMinutes;
    }

    /**
     * 生成包含多角色的token
     */
    public String generateToken(String username, List<String> roles) {
        return JWT.create()
                .withClaim("username", username)
                .withClaim("roles", roles)
                .withExpiresAt(new Date(System.currentTimeMillis() + expireMinutes * 60 * 1000))
                .sign(algorithm);
    }

    /**
     * 验证token有效性
     */
    public boolean validateToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
            return !isTokenExpired(token);
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = decode(token).getExpiresAt();
        return expiration.before(new Date());
    }

    public DecodedJWT decode(String token) {
        return JWT.require(algorithm).build().verify(token);
    }

    public String getUsernameFromToken(String token) {
        return decode(token).getClaim("username").asString();
    }

    public List<String> getRolesFromToken(String token) {
        return decode(token).getClaim("roles").asList(String.class);
    }

    public long getExpirationSeconds() {
        return expireMinutes * 60;
    }
}
