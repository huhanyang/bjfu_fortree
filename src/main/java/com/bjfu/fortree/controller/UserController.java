package com.bjfu.fortree.controller;

import com.bjfu.fortree.dto.user.UserDTO;
import com.bjfu.fortree.dto.user.UserWithAuthoritiesAndWoodlandsDTO;
import com.bjfu.fortree.dto.user.UserWithAuthoritiesDTO;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.request.user.*;
import com.bjfu.fortree.security.annotation.RequireAdmin;
import com.bjfu.fortree.security.annotation.RequireLogin;
import com.bjfu.fortree.service.UserService;
import com.bjfu.fortree.util.SessionUtil;
import com.bjfu.fortree.vo.BaseResult;
import com.bjfu.fortree.vo.PageVO;
import com.bjfu.fortree.vo.user.UserVO;
import com.bjfu.fortree.vo.user.UserWithAuthoritiesAndWoodlandsVO;
import com.bjfu.fortree.vo.user.UserWithAuthoritiesVO;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户相关操作接口
 * @author warthog
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/loginCheck")
    public BaseResult<UserWithAuthoritiesVO> loginCheck(@Validated @RequestBody LoginCheckRequest loginCheckRequest, HttpSession session) {
        UserWithAuthoritiesDTO userWithAuthoritiesDTO = userService.loginCheck(loginCheckRequest);
        if(userWithAuthoritiesDTO == null) {
            return new BaseResult<>(ResultEnum.ACCOUNT_NOT_EXIST_OR_PASSWORD_WRONG);
        }
        SessionUtil.initSession(session, userWithAuthoritiesDTO, loginCheckRequest.getRemember());
        return new BaseResult<>(ResultEnum.SUCCESS, new UserWithAuthoritiesVO(userWithAuthoritiesDTO));
    }

    @PutMapping("/register")
    public BaseResult<UserWithAuthoritiesVO> register(@Validated @RequestBody RegisterRequest registerRequest, HttpSession session) {
        UserWithAuthoritiesDTO userWithAuthoritiesDTO = userService.register(registerRequest);
        if(userWithAuthoritiesDTO == null) {
            return new BaseResult<>(ResultEnum.ACCOUNT_EXIST);
        }
        SessionUtil.initSession(session, userWithAuthoritiesDTO, false);
        return new BaseResult<>(ResultEnum.SUCCESS, new UserWithAuthoritiesVO(userWithAuthoritiesDTO));
    }

    @PostMapping("/changePassword")
    public BaseResult<UserVO> changePassword(@Validated @RequestBody ChangePasswordRequest changePasswordRequest, HttpSession session) {
        UserDTO userDTO = userService.changePassword(SessionUtil.getUserInfo(session).getAccount(), changePasswordRequest);
        if(userDTO == null) {
            return new BaseResult<>(ResultEnum.ACCOUNT_NOT_EXIST_OR_PASSWORD_WRONG);
        }
        SessionUtil.deleteSession(session);
        return new BaseResult<>(ResultEnum.SUCCESS, new UserVO(userDTO));
    }

    @RequireLogin
    @GetMapping("/getInfoWithAuthorities")
    public BaseResult<UserWithAuthoritiesVO> getInfoWithAuthorities(HttpSession session) {
        UserWithAuthoritiesDTO userWithAuthoritiesDTO =
                userService.getUserInfoWithAuthorities(SessionUtil.getUserInfo(session).getAccount());
        return new BaseResult<>(ResultEnum.SUCCESS, new UserWithAuthoritiesVO(userWithAuthoritiesDTO));
    }

    @RequireLogin
    @GetMapping("/getInfoWithAuthoritiesAndWoodlands")
    public BaseResult<UserWithAuthoritiesAndWoodlandsVO> getInfoWithAuthoritiesAndWoodlands(HttpSession session) {
        UserWithAuthoritiesAndWoodlandsDTO userWithAuthoritiesAndWoodlands =
                userService.getUserWithAuthoritiesAndWoodlands(SessionUtil.getUserInfo(session).getAccount());
        return new BaseResult<>(ResultEnum.SUCCESS, new UserWithAuthoritiesAndWoodlandsVO(userWithAuthoritiesAndWoodlands));
    }

    @RequireLogin
    @GetMapping("/getUserInfoWithAuthorities")
    public BaseResult<UserWithAuthoritiesVO> getUserInfoWithAuthorities(@NotBlank(message = "账号不能为空!")
                                                                        @Length(min = 8, max = 32, message = "账号长度在8-32位!")
                                                                        String account) {
        UserWithAuthoritiesDTO userWithAuthoritiesDTO = userService.getUserInfoWithAuthorities(account);
        return new BaseResult<>(ResultEnum.SUCCESS, new UserWithAuthoritiesVO(userWithAuthoritiesDTO));
    }

    @GetMapping("/logout")
    public BaseResult<Void> logout(HttpSession session) {
        SessionUtil.deleteSession(session);
        return new BaseResult<>(ResultEnum.SUCCESS);
    }

    @RequireAdmin
    @PostMapping("/grantUserAuthority")
    public BaseResult<UserWithAuthoritiesVO> grantUserAuthority(@Validated @RequestBody GrantUserAuthorityRequest grantUserAuthorityRequest) {
        UserWithAuthoritiesDTO userWithAuthoritiesDTO =
                userService.grantUserAuthority(grantUserAuthorityRequest);
        return new BaseResult<>(ResultEnum.SUCCESS, new UserWithAuthoritiesVO(userWithAuthoritiesDTO));
    }

    @RequireAdmin
    @PostMapping("/revokeUserAuthority")
    public BaseResult<UserWithAuthoritiesVO> revokeUserAuthority(@Validated @RequestBody RevokeUserAuthorityRequest revokeUserAuthorityRequest) {
        UserWithAuthoritiesDTO userWithAuthoritiesDTO =
                userService.revokeUserAuthority(revokeUserAuthorityRequest);
        return new BaseResult<>(ResultEnum.SUCCESS, new UserWithAuthoritiesVO(userWithAuthoritiesDTO));
    }

    @RequireAdmin
    @PostMapping("/getUsers")
    public BaseResult<PageVO<UserVO>> getUsers(@Validated @RequestBody GetUsersRequest getUsersRequest) {
        PageVO<UserDTO> users = userService.getUsers(getUsersRequest);
        List<UserVO> userVOList = users.getContents().stream()
                .map(UserVO::new)
                .collect(Collectors.toList());
        PageVO<UserVO> pageVO = new PageVO<>(users.getCount(), userVOList);
        return new BaseResult<>(ResultEnum.SUCCESS, pageVO);
    }

    @RequireAdmin
    @PostMapping("/changeUserState")
    public BaseResult<UserVO> changeUserState(@Validated @RequestBody ChangeUserStateRequest changeUserStateRequest,
                                              HttpSession session) {
        if(SessionUtil.getUserInfo(session).getAccount().equals(changeUserStateRequest.getAccount())) {
            return new BaseResult<>(ResultEnum.PARAM_WRONG.getCode(), "不允许操作自己账号的状态");
        }
        UserDTO userDTO = userService.changeUserState(changeUserStateRequest);
        return new BaseResult<>(ResultEnum.SUCCESS, new UserVO(userDTO));
    }

}
