package com.fth.user.service;


import com.fth.user.domain.dto.RegisterRequest;
import com.fth.user.domain.dto.RegisterResponse;

/**
 * 注册服务接口
 */
public interface RegisterService {
    RegisterResponse register(RegisterRequest request);
}