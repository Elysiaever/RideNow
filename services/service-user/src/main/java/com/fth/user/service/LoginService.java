package com.fth.user.service;


import com.fth.user.domain.dto.LoginRequest;
import com.fth.user.domain.dto.LoginResponse;

/**
 * 登录服务接口
 */
public interface LoginService {
    LoginResponse login(LoginRequest request);
}