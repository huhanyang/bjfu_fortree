package com.bjfu.fortree.service;

import com.bjfu.fortree.pojo.dto.user.UserDTO;
import com.bjfu.fortree.pojo.dto.user.UserWithAuthoritiesAndWoodlandsDTO;
import com.bjfu.fortree.pojo.dto.user.UserWithAuthoritiesDTO;
import com.bjfu.fortree.pojo.request.user.*;
import com.bjfu.fortree.pojo.vo.PageVO;

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
     * 获取用户信息
     * @param userAccount 用户账号
     * @return 带有权限的用户信息
     */
    UserWithAuthoritiesDTO getUserInfoWithAuthorities(String userAccount);

    /**
     * 修改密码
     * @param userAccount 用户账号
     * @param changePasswordRequest 更改密码请求
     * @return 用户信息 账号密码不匹配则返回null
     */
    UserDTO changePassword(String userAccount, ChangePasswordRequest changePasswordRequest);

    /**
     * 获取用户信息带有权限列表以及创建的林地列表
     * @param userAccount 用户账号
     * @return 用户信息 有权限列表以及创建的林地列表
     */
    UserWithAuthoritiesAndWoodlandsDTO getUserWithAuthoritiesAndWoodlands(String userAccount);

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

    /**
     * 获取所有用户
     * @param getUsersRequest 请求
     * @return 分页后的用户
     */
    PageVO<UserDTO> getUsers(GetUsersRequest getUsersRequest);

    /**
     * 更改用户的状态
     * @param changeUserStateRequest 请求
     * @return 用户信息
     */
    UserDTO changeUserState(ChangeUserStateRequest changeUserStateRequest);
}
