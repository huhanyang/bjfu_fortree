package com.bjfu.fortree.service;

import com.bjfu.fortree.dto.user.UserDTO;
import com.bjfu.fortree.dto.user.UserWithAuthoritiesDTO;
import com.bjfu.fortree.request.user.*;

/**
 * 用户相关操作
 * @author warthog
 */
public interface UserService {

    /**
     * 登录检查
     * @param loginCheckRequest 登录请求
     * @return 带有权限的用户信息 账号密码不匹配则返回null
     */
    UserWithAuthoritiesDTO loginCheck(LoginCheckRequest loginCheckRequest);

    /**
     * 用户注册
     * @param registerRequest 注册请求
     * @return 带有权限的用户信息 账号已经存在则返回null
     */
    UserWithAuthoritiesDTO register(RegisterRequest registerRequest);

    /**
     * 修改密码
     * @param changePasswordRequest 更改密码请求
     * @return 用户信息 账号密码不匹配则返回null
     */
    UserDTO changePassword(ChangePasswordRequest changePasswordRequest);

    /**
     * 为用户授权
     * @param grantUserAuthorityRequest 授权请求
     * @return 更新后的带有权限的用户信息
     */
    UserWithAuthoritiesDTO grantUserAuthority(GrantUserAuthorityRequest grantUserAuthorityRequest);

    /**
     * 撤销权限
     * @param revokeUserAuthorityRequest 撤销权限请求
     * @return 更新后的带有权限的用户信息
     */
    UserWithAuthoritiesDTO revokeUserAuthority(RevokeUserAuthorityRequest revokeUserAuthorityRequest);
}
