package com.bjfu.fortree.pojo.vo;

import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import com.bjfu.fortree.pojo.dto.AuthorityDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.Optional;

@Data
public class AuthorityVO {

    public AuthorityVO(AuthorityDTO authorityDTO) {
        if(authorityDTO != null) {
            BeanUtils.copyProperties(authorityDTO, this);
            this.user = Optional.ofNullable(authorityDTO.getUser()).map(UserVO::new).orElse(null);
        }
    }

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
    private UserVO user;
    /**
     * 权限类型
     */
    private AuthorityTypeEnum type;
}
