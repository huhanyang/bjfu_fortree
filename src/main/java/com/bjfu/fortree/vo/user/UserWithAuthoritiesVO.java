package com.bjfu.fortree.vo.user;

import com.bjfu.fortree.dto.user.UserWithAuthoritiesDTO;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserWithAuthoritiesVO {

    public UserWithAuthoritiesVO(UserWithAuthoritiesDTO userWithAuthoritiesDTO) {
        BeanUtils.copyProperties(userWithAuthoritiesDTO, this);
        this.authorities = userWithAuthoritiesDTO.getAuthorities().stream()
                .map(AuthorityVO::new)
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
    private List<AuthorityVO> authorities;
}
