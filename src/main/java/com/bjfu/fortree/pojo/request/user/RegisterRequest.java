package com.bjfu.fortree.pojo.request.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
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

    @NotBlank(message = "电话号码不能为空!")
    @Length(min = 11, max = 11, message = "电话号码长度为11个字符!")
    private String phone;

    @NotBlank(message = "邮箱地址不能为空!")
    @Email(message = "请输入正确的邮箱！")
    @Length(min = 1, max = 32, message = "邮箱长度1到32个字符!")
    private String email;
}
