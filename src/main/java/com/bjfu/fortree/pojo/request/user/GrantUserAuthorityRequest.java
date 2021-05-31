package com.bjfu.fortree.pojo.request.user;

import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 为用户授权
 * @author warthog
 */
@Data
public class GrantUserAuthorityRequest {

    @NotBlank(message = "目标账号不能为空!")
    @Length(min = 8, max = 32, message = "账号长度在8-32位!")
    private String account;

    @NotNull(message = "授予的权限列表不能为空!")
    private List<AuthorityTypeEnum> authorities;

}
