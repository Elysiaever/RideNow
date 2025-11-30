package com.fth.user.service.impl;

import com.fth.common.core.constant.ExceptionConstant;
import com.fth.common.exception.BusinessException;
import com.fth.user.domain.dto.LoginRequest;
import com.fth.user.domain.dto.LoginResponse;
import com.fth.user.domain.model.User;
import com.fth.user.service.LoginService;
import com.fth.user.service.UserService;
import com.fth.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 如果密码加密
import org.springframework.stereotype.Service;
import com.fth.common.core.constant.ExceptionConstant;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    // 如果密码是明文存储，可以不用
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(LoginRequest request) {
        System.out.println("开始登录");

        User user = userService.getByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(401,ExceptionConstant.USER_NOT_FOUND));
        System.out.println("数据库密码: " + user.getPassword());
        System.out.println("输入密码: " + request.getPassword());
        System.out.println("匹配结果: " + passwordEncoder.matches(request.getPassword(), user.getPassword()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("密码错误");

            throw new BusinessException(401,ExceptionConstant.PASSWORD_ERROR);
        }


        String token = jwtTokenProvider.generateToken(user.getUsername());
        Long expirationSeconds = jwtTokenProvider.getExpirationSeconds();

        return new LoginResponse(
                token,
                expirationSeconds,
                user.getUsername()
        );
    }

}
