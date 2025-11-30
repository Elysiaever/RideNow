package com.fth.user.controller;

import com.fth.user.domain.dto.LoginRequest;
import com.fth.user.domain.dto.LoginResponse;

import com.fth.user.domain.dto.RegisterRequest;
import com.fth.user.domain.dto.RegisterResponse;
import com.fth.user.service.LoginService;
import com.fth.user.service.RegisterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户认证控制器：处理登录和注册请求
 */
@RestController
@RequestMapping("/api/user")
public class LoginController {


    private final LoginService loginService;
    private final RegisterService registerService;

    public LoginController(LoginService loginService, RegisterService registerService) {
        this.loginService = loginService;
        this.registerService = registerService;
    }

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = registerService.register(request);
        return ResponseEntity.ok(response);
    }
}