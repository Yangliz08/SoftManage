package com.softmanage.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String username;
    private String realName;
    private String email;
    private String avatar;
    private String accessToken;
    private String refreshToken;
    private List<String> roles;
    private List<String> permissions;
}

