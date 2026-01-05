package com.fth.user.controller;

import com.fth.common.api.Response;
import com.fth.common.feign.driver.DriverFeignClient;
import com.fth.user.domain.dto.*;

import com.fth.user.domain.model.User;
import com.fth.user.service.LoginService;
import com.fth.user.service.RegisterService;
import com.fth.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


/**
 * 用户认证控制器：处理登录和注册请求
 */
@RestController
@RequestMapping("/api/user")
public class LoginController {


    private final LoginService loginService;
    private final RegisterService registerService;
    private final UserService userService;

    @Autowired
    DriverFeignClient driverFeignClient;

    public LoginController(LoginService loginService, RegisterService registerService, UserService userService) {
        this.loginService = loginService;
        this.registerService = registerService;
        this.userService = userService;
    }

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public Response<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginService.login(request);
        return Response.success(response);
    }

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public Response<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = registerService.register(request);
        return Response.success(response);
    }

    @GetMapping("/info")
    public Response<UserInfo> getUserById(@RequestParam String userId) {
        User user = userService.getUserById(Long.valueOf(userId));
        if (user == null) {
            return Response.error(404, "用户不存在");
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setPhone(user.getPhone());
        userInfo.setRole(driverFeignClient.isDriver(user.getId()) ? "DRIVER" : "USER");
        
        // 设置其他用户信息字段
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        
        // 设置角色数组
        String role = driverFeignClient.isDriver(user.getId()) ? "DRIVER" : "USER";
        userInfo.setRoles(new String[]{role});
        
        return Response.success(userInfo);
    }
}