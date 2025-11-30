package com.fth.user.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 注册请求DTO：接收前端用户名和密码
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 50, message = "用户名长度需在4-50个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度需在6-20个字符之间")
    private String password;

    // 手机号可选（如需必填，移除@Nullable并添加@Pattern验证）
    private String phone;
}