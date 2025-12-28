package com.fth.user.domain.dto;

import lombok.Data;


@Data
public class UserInfo {
    private Long id;
    private String username;
    private String phone;
    private String role;
}
