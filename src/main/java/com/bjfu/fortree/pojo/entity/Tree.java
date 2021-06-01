package com.bjfu.fortree.pojo.entity;

import com.bjfu.fortree.pojo.entity.BaseEntity;
import com.bjfu.fortree.pojo.entity.Record;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 单棵树木实体类
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
    @Column(length=32, nullable=false)
    private String treeId;
    /**
     * 树种
     */
    @Column(length=32, nullable=false)
    private String species;
    /**
     * 高度 厘米
     */
    @Column(nullable=false)
    private Double height;
    /**
     * 胸径 厘米
     */
    @Column(nullable=false)
    private Double dbh;
    /**
     * 冠幅 厘米
     */
    @Column(nullable=false)
    private Double crownWidth;

    /**
     * 附加信息(JSON)
     */
    @Column(length=512)
    private String addition;
}
