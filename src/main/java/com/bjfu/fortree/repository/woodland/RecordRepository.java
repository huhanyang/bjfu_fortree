package com.bjfu.fortree.repository.woodland;

import com.bjfu.fortree.pojo.entity.woodland.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

/**
 * 林地记录的持久接口
 * @author warthog
 */
public interface RecordRepository extends JpaRepository<Record, Long> {

    /**
     * 根据id查找林地记录实体并加锁
     * @param id id
     * @return 林地记录实体
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select record from Record record where record.id=?1")
    Optional<Record> findByIdForUpdate(Long id);
}
