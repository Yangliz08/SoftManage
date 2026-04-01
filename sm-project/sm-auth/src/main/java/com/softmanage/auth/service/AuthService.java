package com.softmanage.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.softmanage.auth.dto.*;
import com.softmanage.auth.entity.SysPermission;
import com.softmanage.auth.entity.SysRole;
import com.softmanage.auth.entity.SysUser;
import com.softmanage.auth.mapper.SysPermissionMapper;
import com.softmanage.auth.mapper.SysRoleMapper;
import com.softmanage.auth.mapper.SysUserMapper;
import com.softmanage.common.dto.PageResponse;
import com.softmanage.common.exception.BusinessException;
import com.softmanage.common.result.ResultCode;
import com.softmanage.common.utils.JwtUtil;
import com.softmanage.common.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        SysUser user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }

        // 查询角色
        List<SysRole> roles = roleMapper.findRolesByUserId(user.getId());
        String primaryRole = roles.isEmpty() ? "DEV" : roles.get(0).getRoleCode();
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());

        // 查询权限
        List<SysPermission> permissions = permissionMapper.findPermissionsByUserId(user.getId());
        List<String> permCodes = permissions.stream().map(SysPermission::getPermCode).collect(Collectors.toList());

        // 生成 Token
        String accessToken = JwtUtil.generateToken(user.getId(), user.getUsername(), primaryRole);
        String refreshToken = JwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        // 存 Redis
        redisUtil.set("token:" + user.getId(), accessToken, 24, TimeUnit.HOURS);

        // 更新登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(roleCodes)
                .permissions(permCodes)
                .build();
    }

    /**
     * 退出登录
     */
    public void logout(Long userId) {
        redisUtil.delete("token:" + userId);
    }

    /**
     * 刷新 Token
     */
    public LoginResponse refreshToken(String refreshToken) {
        if (!JwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        }
        Long userId = JwtUtil.getUserId(refreshToken);
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        List<SysRole> roles = roleMapper.findRolesByUserId(user.getId());
        String primaryRole = roles.isEmpty() ? "DEV" : roles.get(0).getRoleCode();
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());
        List<SysPermission> permissions = permissionMapper.findPermissionsByUserId(user.getId());
        List<String> permCodes = permissions.stream().map(SysPermission::getPermCode).collect(Collectors.toList());

        String newAccessToken = JwtUtil.generateToken(user.getId(), user.getUsername(), primaryRole);
        String newRefreshToken = JwtUtil.generateRefreshToken(user.getId(), user.getUsername());
        redisUtil.set("token:" + user.getId(), newAccessToken, 24, TimeUnit.HOURS);

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .roles(roleCodes)
                .permissions(permCodes)
                .build();
    }

    /**
     * 获取当前用户信息
     */
    public UserVO getUserInfo(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        List<SysRole> roles = roleMapper.findRolesByUserId(userId);
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());

        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .lastLoginTime(user.getLastLoginTime())
                .roles(roleCodes)
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * 创建用户
     */
    @Transactional
    public UserVO createUser(UserCreateRequest request) {
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        userMapper.insert(user);

        // 分配角色
        if (request.getRoleCode() != null) {
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRole::getRoleCode, request.getRoleCode());
            SysRole role = roleMapper.selectOne(wrapper);
            if (role != null) {
                roleMapper.insertUserRole(user.getId(), role.getId());
            }
        }

        return getUserInfo(user.getId());
    }

    /**
     * 用户列表（分页）
     */
    public PageResponse<UserVO> getUserList(int page, int size, String keyword) {
        Page<SysUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword);
        }
        wrapper.orderByDesc(SysUser::getCreatedAt);
        Page<SysUser> result = userMapper.selectPage(pageParam, wrapper);

        List<UserVO> records = result.getRecords().stream().map(u -> {
            List<SysRole> roles = roleMapper.findRolesByUserId(u.getId());
            return UserVO.builder()
                    .id(u.getId())
                    .username(u.getUsername())
                    .realName(u.getRealName())
                    .email(u.getEmail())
                    .phone(u.getPhone())
                    .status(u.getStatus())
                    .roles(roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList()))
                    .createdAt(u.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());

        return PageResponse.of(records, result.getTotal(), page, size);
    }

    /**
     * 更新用户状态
     */
    public void updateUserStatus(Long userId, Integer status) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }

    /**
     * 获取所有角色
     */
    public List<SysRole> getAllRoles() {
        return roleMapper.selectList(new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, 1));
    }

    /**
     * 获取用户权限列表
     */
    public List<SysPermission> getUserPermissions(Long userId) {
        return permissionMapper.findPermissionsByUserId(userId);
    }
}

