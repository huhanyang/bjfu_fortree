package com.bjfu.fortree.repository.user;

import com.bjfu.fortree.entity.user.Authority;
import com.bjfu.fortree.entity.user.User;
import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 权限的持久接口
 * @author warthog
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    /**
     * 根据用户和类型判断权限是否存在
     * @param user 用户实体
     * @param type 权限类型枚举
     * @return 是否存在
     */
    boolean existsByUserAndType(User user, AuthorityTypeEnum type);

}