package com.fth.user.controller;  // 修正导入包路径

import com.fth.user.dto.LoginRequest;  // 修正
import com.fth.user.dto.LoginResponse;  // 修正
import com.fth.user.security.InMemoryUserDetailsService;
import com.fth.user.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class AuthController {
    // 内容保持不变，仅修正导入路径
    private final AuthenticationManager authManager;
    private final InMemoryUserDetailsService userService;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(AuthenticationManager authManager,
                          InMemoryUserDetailsService userService,
                          JwtUtils jwtUtils) {
        this.authManager = authManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        // 方法内容不变
        if (req.getUsername() == null || req.getPassword() == null || req.getRole() == null) {
            return ResponseEntity.badRequest().body("username, password and role are required");
        }

        var optional = userService.findAppUser(req.getUsername());
        if (optional.isEmpty()) {
            return ResponseEntity.status(401).body("invalid credentials");
        }

        var appUser = optional.get();
        // 密码校验处添加日志
        boolean passwordMatch = encoder.matches(req.getPassword(), appUser.getPassword());
        System.out.println("密码是否匹配：" + passwordMatch); // 检查是否为false
        if (!passwordMatch) {
            return ResponseEntity.status(401).body("invalid credentials");
        }
        if (!encoder.matches(req.getPassword(), appUser.getPassword())) {
            return ResponseEntity.status(401).body("invalid credentials");
        }

        if (!appUser.getRoles().contains(req.getRole())) {
            return ResponseEntity.status(403).body("user does not have role: " + req.getRole());
        }

        try {
            Authentication authenticate = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (AuthenticationException ex) {
            // 保持不变
        }

        String token = jwtUtils.generateToken(appUser.getUsername(), appUser.getRoles());  // 修正参数
        LoginResponse resp = new LoginResponse(token, jwtUtils.getExpirationSeconds(), appUser.getUsername(), appUser.getRoles());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        // 方法内容不变
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("missing token");
        }
        String token = header.substring(7);
        if (!jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).body("invalid token");
        }

        String username = jwtUtils.getUsernameFromToken(token);
        List<String> roles = jwtUtils.getRolesFromToken(token);
        return ResponseEntity.ok(new LoginResponse(token, jwtUtils.getExpirationSeconds(), username, roles));
    }
}