package com.bjfu.fortree.dto.user;

import com.bjfu.fortree.entity.user.User;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserDTO {

    public UserDTO(User user) {
        BeanUtils.copyProperties(user, this);
    }

    /**
     * 用户名
     */
    private String account;
    /**
     * 姓名
     */
    private String name;
    /**
     * 所属组织名
     */
    private String organization;
    /**
     * 账号类型
     */
    private UserTypeEnum type;
}
