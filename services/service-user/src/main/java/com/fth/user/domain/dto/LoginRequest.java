package com.fth.user.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求DTO：接收前端用户名和密码
 */
@Data
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}