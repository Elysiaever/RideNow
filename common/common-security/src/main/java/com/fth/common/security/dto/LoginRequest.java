package com.fth.common.security.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username; // 用户名/手机号/邮箱
    private String password; // 密码
}
