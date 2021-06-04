package com.bjfu.fortree.service;

import com.bjfu.fortree.pojo.dto.UserDTO;
import com.bjfu.fortree.pojo.request.user.*;
import org.springframework.data.domain.Page;

/**
 * 用户相关操作
 * @author warthog
 */
public interface UserService {

    /**
     * 登录
     * @param request 登录请求
     * @return 用户信息
     */
    UserDTO login(LoginCheckRequest request);

    /**
     * 注册
     * @param request 注册请求
     * @return 用户信息
     */
    UserDTO register(RegisterRequest request);

    /**
     * 获取用户信息
     * @param userAccount 账号
     * @return 用户信息
     */
    UserDTO getInfo(String userAccount);

    /**
     * 为上下文获取用户信息
     * @param userAccount 账号
     * @return 用户信息
     */
    UserDTO getInfoForContext(String userAccount);

    /**
     * 修改密码
     * @param userAccount 用户账号
     * @param request 更改密码请求
     */
    void changePassword(String userAccount, ChangePasswordRequest request);

    /**
     * 为用户授权
     * @param request 授权请求
     */
    void grantUserAuthority(GrantUserAuthorityRequest request);

    /**
     * 撤销权限
     * @param request 撤销权限请求
     */
    void revokeUserAuthority(RevokeUserAuthorityRequest request);

    /**
     * 分页获取用户信息
     * @param request 请求
     * @return 分页后的用户信息
     */
    Page<UserDTO> getUsers(GetUsersRequest request);

    /**
     * 更改用户的状态
     * @param request 请求
     */
    void changeUserState(ChangeUserStateRequest request);
}
