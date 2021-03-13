package com.bjfu.fortree.repository.user;

import com.bjfu.fortree.pojo.entity.user.User;
import com.bjfu.fortree.enums.entity.UserStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

/**
 * 用户的持久接口
 * @author warthog
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 根据账号查找用户实体
     * @param account 账号
     * @return 用户实体
     */
    Optional<User> findByAccount(String account);

    /**
     * 根据账号查找用户实体并加锁
     * @param account 账号
     * @return 用户实体
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select user from User user where user.account=?1")
    Optional<User> findByAccountForUpdate(String account);

    /**
     * 判断是否存在账号和状态匹配的用户(判断是否封号或活跃)
     * @param account 用户账号
     * @param state 用户状态
     * @return 是否存在
     */
    boolean existsByAccountAndState(String account, UserStateEnum state);
}
