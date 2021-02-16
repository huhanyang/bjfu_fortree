package com.bjfu.fortree.repository.woodland;

import com.bjfu.fortree.entity.user.User;
import com.bjfu.fortree.entity.woodland.Woodland;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Polygon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * 林地的持久接口
 * @author warthog
 */
public interface WoodlandRepository extends JpaRepository<Woodland, Long>, JpaSpecificationExecutor<Woodland> {

    /**
     * 根据id查找林地实体
     * @param id 林地id
     * @return 林地实体
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select woodland from Woodland woodland where woodland.id=?1")
    Optional<Woodland> findByIdForUpdate(Long id);

    /**
     * 根据创建人查找创建的林地
     * @param creator 创建人
     * @return 林地实体列表
     */
    List<Woodland> findByCreator(User creator);

    /**
     * 查询矩形范围内的林地列表
     * @param polygon 矩形范围
     * @return 林地列表
     */
    @Query(value = "select woodland from Woodland woodland where within(woodland.position, ?1) = true")
    List<Woodland> findWoodlandsInPolygon(Polygon<G2D> polygon);

}