package com.softmanage.auth.controller;

import com.softmanage.auth.dto.*;
import com.softmanage.auth.entity.SysPermission;
import com.softmanage.auth.entity.SysRole;
import com.softmanage.auth.service.AuthService;
import com.softmanage.common.constant.AuthConstant;
import com.softmanage.common.dto.PageResponse;
import com.softmanage.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        authService.logout(userId);
        return Result.success();
    }

    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        return Result.success(authService.refreshToken(refreshToken));
    }

    @GetMapping("/user/info")
    public Result<UserVO> getUserInfo(@RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        return Result.success(authService.getUserInfo(userId));
    }

    @GetMapping("/user/permissions")
    public Result<List<SysPermission>> getUserPermissions(@RequestHeader(AuthConstant.USER_ID_HEADER) Long userId) {
        return Result.success(authService.getUserPermissions(userId));
    }

    @GetMapping("/user/list")
    public Result<PageResponse<UserVO>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(authService.getUserList(page, size, keyword));
    }

    @PostMapping("/user/create")
    public Result<UserVO> createUser(@Valid @RequestBody UserCreateRequest request) {
        return Result.success(authService.createUser(request));
    }

    @PutMapping("/user/{userId}/status")
    public Result<Void> updateUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        authService.updateUserStatus(userId, status);
        return Result.success();
    }

    @DeleteMapping("/user/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        authService.deleteUser(userId);
        return Result.success();
    }

    @GetMapping("/roles")
    public Result<List<SysRole>> getAllRoles() {
        return Result.success(authService.getAllRoles());
    }
}

