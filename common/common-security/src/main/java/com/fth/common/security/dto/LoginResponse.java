package com.fth.common.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token; // JWT令牌
    private String tokenType = "Bearer"; // 令牌类型
    private long expiresIn; // 过期时间（秒）
    private String username; // 用户名
    private List<String> roles; // 角色列表
}