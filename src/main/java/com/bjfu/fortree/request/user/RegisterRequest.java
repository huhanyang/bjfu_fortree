package com.bjfu.fortree.request.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 注册请求
 * @author warthog
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "账号不能为空!")
    @Length(min = 8, max = 32, message = "账号长度在8-32位!")
    private String account;

    @NotBlank(message = "密码不能为空!")
    @Length(min = 8, max = 32, message = "密码长度在8-32位!")
    private String password;

    @NotBlank(message = "真实姓名不能为空!")
    @Length(min = 1, max = 32, message = "真实姓名长度1到32个字符!")
    private String name;

    @NotBlank(message = "组织名不能为空!")
    @Length(min = 1, max = 32, message = "组织名长度1到32个字符!")
    private String organization;
}
