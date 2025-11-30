package com.fth.user.controller;

import com.fth.common.security.jwt.JwtTokenProvider;
import com.fth.user.dto.LoginRequest;
import com.fth.user.dto.LoginResponse;
import com.fth.user.security.InMemoryUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class AuthController {
    private final AuthenticationManager authManager;
    private final InMemoryUserDetailsService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(AuthenticationManager authManager,
                          InMemoryUserDetailsService userService,
                          JwtTokenProvider jwtTokenProvider) {
        this.authManager = authManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (req.getUsername() == null || req.getPassword() == null || req.getRole() == null) {
            return ResponseEntity.badRequest().body("username, password and role are required");
        }

        try {
            Authentication authenticate = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authenticate);

            var appUser = userService.findAppUser(req.getUsername()).orElseThrow();
            if (!appUser.getRoles().contains(req.getRole())) {
                return ResponseEntity.status(403).body("user does not have role: " + req.getRole());
            }

            String token = jwtTokenProvider.generateToken(appUser.getUsername(), appUser.getRoles());
            LoginResponse resp = new LoginResponse(
                    token,
                    jwtTokenProvider.getExpirationSeconds(),
                    appUser.getUsername(),
                    appUser.getRoles()
            );
            return ResponseEntity.ok(resp);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("invalid credentials");
        }
    }
}