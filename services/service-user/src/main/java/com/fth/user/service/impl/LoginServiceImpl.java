package com.fth.user.service.impl;

import com.fth.user.domain.dto.LoginRequest;
import com.fth.user.domain.dto.LoginResponse;
import com.fth.user.domain.model.User;
import com.fth.user.service.LoginService;
import com.fth.user.service.UserService;
import com.fth.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 如果密码加密
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    // 如果密码是明文存储，可以不用
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(LoginRequest request) {

        // 1. 查询用户信息
        User user = userService.getByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名不存在"));


         if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
             throw new RuntimeException("密码错误");
         }

        // 3. 生成 JWT
        String token = jwtTokenProvider.generateToken(user.getUsername());
        Long expirationSeconds = jwtTokenProvider.getExpirationSeconds();

        // 4. 返回登录结果
        return new LoginResponse(
                token,
                expirationSeconds,
                user.getUsername()
        );
    }
}
