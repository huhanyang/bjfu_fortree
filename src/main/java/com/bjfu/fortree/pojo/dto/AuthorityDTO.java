package com.bjfu.fortree.pojo.dto;

import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import com.bjfu.fortree.pojo.entity.Authority;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class AuthorityDTO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 修改时间
     */
    private Date lastModifiedTime;
    /**
     * 权限所属的用户
     */
    private UserDTO user;
    /**
     * 权限类型
     */
    private AuthorityTypeEnum type;

    public AuthorityDTO(Authority authority, Boolean needUser) {
        if (authority != null) {
            BeanUtils.copyProperties(authority, this, "user");
            if (needUser) {
                this.user = new UserDTO(authority.getUser(), false, false, false, false);
            }
        }
    }
}
