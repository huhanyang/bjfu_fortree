package com.bjfu.fortree.entity.user;

import com.bjfu.fortree.entity.BaseEntity;
import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 用户权限实体类
 * @author warthog
 */
@Getter
@Setter
@Entity
@Table(name = "fortree_user_authority")
public class Authority extends BaseEntity {

    /**
     * 权限所属的用户
     */
    @ManyToOne
    private User user;

    /**
     * 权限类型
     */
    @Enumerated
    @Column(nullable=false)
    private AuthorityTypeEnum type;

}
