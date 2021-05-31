package com.bjfu.fortree.controller;

import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.pojo.dto.user.UserDTO;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.pojo.request.user.*;
import com.bjfu.fortree.security.annotation.RequireAdmin;
import com.bjfu.fortree.security.annotation.RequireLogin;
import com.bjfu.fortree.service.UserService;
import com.bjfu.fortree.pojo.vo.BaseResult;
import com.bjfu.fortree.pojo.vo.user.UserVO;
import com.bjfu.fortree.util.JwtUtil;
import com.bjfu.fortree.util.UserInfoContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

/**
 * 用户相关操作接口
 * @author warthog
 */
@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public BaseResult<UserVO> login(@Validated @RequestBody LoginCheckRequest request) {
        UserDTO userDTO = userService.login(request);
        // 生成Token
        String token = JwtUtil.generateToken(Collections.singletonMap("userAccount", userDTO.getAccount()));
        return new BaseResult<>(ResultEnum.SUCCESS, new UserVO(userDTO, token));
    }

    @PostMapping("/register")
    public BaseResult<UserVO> register(@Validated @RequestBody RegisterRequest request) {
        UserDTO userDTO = userService.register(request);
        // 生成Token
        String token = JwtUtil.generateToken(Collections.singletonMap("userAccount", userDTO.getAccount()));
        return new BaseResult<>(ResultEnum.SUCCESS, new UserVO(userDTO, token));
    }

    @RequireLogin
    @PostMapping("/changePassword")
    public BaseResult<Void> changePassword(@Validated @RequestBody ChangePasswordRequest request) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        userService.changePassword(userInfo.getAccount(), request);
        return new BaseResult<>(ResultEnum.SUCCESS);
    }

    @RequireLogin
    @GetMapping("/getInfo")
    public BaseResult<UserVO> getInfo() {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        UserDTO userDTO = userService.getInfo(userInfo.getAccount());
        return new BaseResult<>(ResultEnum.SUCCESS, new UserVO(userDTO));
    }

    @RequireAdmin
    @PostMapping("/grantUserAuthority")
    public BaseResult<Void> grantUserAuthority(@Validated @RequestBody GrantUserAuthorityRequest request) {
        userService.grantUserAuthority(request);
        return new BaseResult<>(ResultEnum.SUCCESS);
    }

    @RequireAdmin
    @PostMapping("/revokeUserAuthority")
    public BaseResult<Void> revokeUserAuthority(@Validated @RequestBody RevokeUserAuthorityRequest request) {
        userService.revokeUserAuthority(request);
        return new BaseResult<>(ResultEnum.SUCCESS);
    }

    @RequireAdmin
    @PostMapping("/getUsers")
    public BaseResult<Page<UserVO>> getUsers(@Validated @RequestBody GetUsersRequest request) {
        Page<UserDTO> userDTOS = userService.getUsers(request);
        return new BaseResult<>(ResultEnum.SUCCESS, userDTOS.map(UserVO::new));
    }

    @RequireAdmin
    @PostMapping("/changeUserState")
    public BaseResult<Void> changeUserState(@Validated @RequestBody ChangeUserStateRequest request) {
        userService.changeUserState(request);
        return new BaseResult<>(ResultEnum.SUCCESS);
    }

}
