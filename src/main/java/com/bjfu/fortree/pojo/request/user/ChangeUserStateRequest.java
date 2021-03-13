package com.bjfu.fortree.pojo.request.user;

import com.bjfu.fortree.enums.entity.UserStateEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 修改用户状态请求
 * @author warthog
 */
@Data
public class ChangeUserStateRequest {

    @NotBlank(message = "账号不能为空!")
    @Length(min = 8, max = 32, message = "账号长度在8-32位!")
    String account;

    @NotNull(message = "新状态不能为空!")
    UserStateEnum newState;

}
