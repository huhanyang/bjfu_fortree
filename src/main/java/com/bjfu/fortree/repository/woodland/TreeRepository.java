package com.bjfu.fortree.repository.woodland;

import com.bjfu.fortree.entity.woodland.Tree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

/**
 * 树木的持久接口
 * @author warthog
 */
public interface TreeRepository extends JpaRepository<Tree, Long> {

    /**
     * 根据id集合删除树木实体
     * @param ids 要删除的id集合
     */
    void deleteByIdIn(Collection<Long> ids);

}
