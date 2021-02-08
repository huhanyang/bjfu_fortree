package com.bjfu.fortree.vo.user;

import com.bjfu.fortree.dto.user.UserDTO;
import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author warthog
 */
@Data
public class UserVO {

    public UserVO(UserDTO userDTO) {
        BeanUtils.copyProperties(userDTO, this);
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
    /**
     * 账号状态
     */
    private UserStateEnum state;
}
