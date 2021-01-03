package com.bjfu.fortree.entity.woodland;

import com.bjfu.fortree.entity.BaseEntity;
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
     * 胸径
     */
    @Column(nullable=false)
    private Integer height;
    /**
     * 冠幅
     */
    @Column(nullable=false)
    private Integer crownDiameter;

    /**
     * 附加信息(JSON)
     */
    @Column(length=512)
    private String addition;
}
