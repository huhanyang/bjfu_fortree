package com.bjfu.fortree.pojo.request.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 修改密码请求
 * @author warthog
 */
@Data
public class ChangePasswordRequest {

    @NotBlank(message = "原密码不能为空!")
    @Length(min = 8, max = 32, message = "密码长度在8-32位!")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空!")
    @Length(min = 8, max = 32, message = "密码长度在8-32位!")
    private String newPassword;

}
