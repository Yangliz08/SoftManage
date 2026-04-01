package com.softmanage.common.constant;

/**
 * 认证相关常量
 */
public class AuthConstant {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USERNAME_HEADER = "X-Username";
    public static final String ROLE_HEADER = "X-Role";

    // 角色
    public static final String ROLE_PM = "PM";
    public static final String ROLE_DEV = "DEV";
    public static final String ROLE_TEST = "TEST";
    public static final String ROLE_ADMIN = "ADMIN";
}

