package com.fth.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 注册响应DTO：返回注册结果
 */
@Data
@AllArgsConstructor
public class RegisterResponse {
    private Long userId; // 用户ID
    private String username; // 用户名
    private String message; // 提示信息
}