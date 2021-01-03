package com.bjfu.fortree.repository.woodland;

import com.bjfu.fortree.entity.woodland.Woodland;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

/**
 * 林地的持久接口
 * @author warthog
 */
public interface WoodlandRepository extends JpaRepository<Woodland, Long> {

    /**
     * 根据id查找林地实体
     * @param id 林地id
     * @return 林地实体
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select woodland from Woodland woodland where woodland.id=?1")
    Optional<Woodland> findByIdForUpdate(Long id);

}