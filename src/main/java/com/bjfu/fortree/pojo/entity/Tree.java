package com.bjfu.fortree.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 单棵树木实体类
 *
 * @author warthog
 */
@Getter
@Setter
@Entity
@Table(name = "fortree_woodland_tree")
public class Tree extends BaseEntity {
    /**
     * 所属记录
     */
    @ManyToOne
    private Record record;
    /**
     * 树的编号
     */
    @Column(length = 32)
    private String treeId;
    /**
     * 树种
     */
    @Column(length = 32, nullable = false)
    private String species;
    /**
     * 高度 厘米
     */
    @Column(nullable = false)
    private Double height;
    /**
     * 胸径 厘米
     */
    private Double dbh;
    /**
     * 冠幅 厘米
     */
    private Double crownWidth;
    /**
     * 枝下高 厘米
     */
    private Double subbranchHeight;
    /**
     * 绝对坐标
     */
    @Column()
    private Point<G2D> absolutePosition;
    /**
     * 附加信息(JSON)
     */
    @Column(length = 512)
    private String addition;
}
