package com.fth.user.dto;  // 修正包路径

import java.util.List;

public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private long expiresIn; // seconds
    private String username;
    private List<String> roles;

    public LoginResponse() {}

    public LoginResponse(String token, long expiresIn, String username, List<String> roles) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.username = username;
        this.roles = roles;
    }

    // getter/setter 保持不变
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}