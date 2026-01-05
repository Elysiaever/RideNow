package com.fth.user.domain.dto;

import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String[] roles;
    private String avatar;
    private String gender;
    private String birthday;
    private String role;
}
