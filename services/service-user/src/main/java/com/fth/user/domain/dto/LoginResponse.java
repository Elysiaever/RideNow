package com.fth.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应DTO：返回令牌、用户名等信息
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token; // JWT令牌
    private Long expirationSeconds; // 令牌过期时间（秒）
    private String username; // 用户名
}