package com.bjfu.fortree.pojo.request.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求
 *
 * @author warthog
 */
@Data
public class LoginCheckRequest {

    @NotBlank(message = "账号不能为空!")
    @Length(min = 8, max = 32, message = "账号长度在8-32位!")
    private String account;

    @NotBlank(message = "密码不能为空!")
    @Length(min = 8, max = 32, message = "密码长度在8-32位!")
    private String password;

}
