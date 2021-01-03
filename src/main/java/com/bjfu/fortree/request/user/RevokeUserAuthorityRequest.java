package com.bjfu.fortree.request.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 撤销用户权限的请求
 * @author warthog
 */
@Data
public class RevokeUserAuthorityRequest {

    @NotBlank(message = "目标账号不能为空!")
    @Length(min = 8, max = 32, message = "账号长度在8-32位!")
    private String account;

    @NotNull(message = "撤销的权限列表不能为空!")
    private String[] authorities;

}
