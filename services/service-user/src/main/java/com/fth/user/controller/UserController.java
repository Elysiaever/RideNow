package com.fth.user.controller;

import com.fth.common.api.Response;
import com.fth.user.domain.dto.UserInfo;
import com.fth.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/update/{userId}")
    public Response<Void> updateUserInfo(@PathVariable String userId, @RequestBody Map<String, Object> userInfo) {
        userService.updateUserInfo(Long.valueOf(userId), userInfo);
        return Response.success(null);
    }

    @PostMapping("/change-password/{userId}")
    public Response<Void> changePassword(@PathVariable String userId, @RequestBody Map<String, String> passwordData) {
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        userService.changePassword(Long.valueOf(userId), oldPassword, newPassword);
        return Response.success(null);
    }
}