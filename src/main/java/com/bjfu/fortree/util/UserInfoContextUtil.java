package com.bjfu.fortree.util;

import com.bjfu.fortree.pojo.dto.UserDTO;

import java.util.Optional;

public class UserInfoContextUtil {

    private static final ThreadLocal<UserDTO> userInfoThreadLocal = new ThreadLocal<>();

    public static Optional<UserDTO> getUserInfo() {
        return Optional.ofNullable(userInfoThreadLocal.get());
    }

    public static void setUserInfo(UserDTO userDTO) {
        userInfoThreadLocal.set(userDTO);
    }

    public static void clear() {
        userInfoThreadLocal.remove();
    }

}
