package com.bjfu.fortree.vo.user;

import com.bjfu.fortree.dto.user.AuthorityDTO;
import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AuthorityVO {

    public AuthorityVO(AuthorityDTO authorityDTO) {
        BeanUtils.copyProperties(authorityDTO, this);
    }

    /**
     * 权限类型
     */
    AuthorityTypeEnum type;
}
