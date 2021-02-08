package com.bjfu.fortree.repository.job;

import com.bjfu.fortree.entity.apply.ApplyJob;
import com.bjfu.fortree.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * 申请实体的持久接口
 * @author warthog
 */
public interface ApplyJobRepository extends JpaRepository<ApplyJob, Long>, JpaSpecificationExecutor<ApplyJob> {

    /**
     * 根据id查找并加锁
     * @param id id
     * @return 申请实体
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select applyjob from ApplyJob applyjob where applyjob.id=?1")
    Optional<ApplyJob> findByIdForUpdate(Long id);

    /**
     * 根据申请人查询
     * @param applyUser 申请人
     * @return 申请实体列表
     */
    List<ApplyJob> findByApplyUser(User applyUser);
}
