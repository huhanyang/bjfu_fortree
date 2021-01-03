package com.bjfu.fortree.dto.user;

import com.bjfu.fortree.entity.user.User;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserWithAuthoritiesDTO {

    public UserWithAuthoritiesDTO(User user) {
        BeanUtils.copyProperties(user, this);
        this.authorities = user.getAuthorities().stream()
                .map(AuthorityDTO::new)
                .collect(Collectors.toList());
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
     * 拥有的权限
     */
    private List<AuthorityDTO> authorities;
}
