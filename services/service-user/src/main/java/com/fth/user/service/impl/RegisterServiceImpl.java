package com.fth.user.service.impl;


import com.fth.user.domain.dto.RegisterRequest;
import com.fth.user.domain.dto.RegisterResponse;
import com.fth.user.domain.model.User;
import com.fth.user.service.RegisterService;
import com.fth.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * 注册服务实现类
 */
@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterResponse register(RegisterRequest request) {
        // 1. 校验用户名
        if (userService.isUsernameExists(request.getUsername())) {
            throw new RuntimeException("用户名已被注册");
        }

        // 2. 校验手机号
        if (userService.isPhoneExists(request.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }

        // 3. 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 4. 保存用户
        userService.save(user);

        // 5. 返回结果
        return new RegisterResponse(
                user.getId(),
                user.getUsername(),
                "注册成功"
        );
    }
}