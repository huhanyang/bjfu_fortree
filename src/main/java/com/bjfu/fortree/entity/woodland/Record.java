package com.bjfu.fortree.entity.woodland;

import com.bjfu.fortree.entity.BaseEntity;
import com.bjfu.fortree.entity.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 林地记录实体类
 * @author warthog
 */
@Getter
@Setter
@Entity
@Table(name = "fortree_woodland_record")
public class Record extends BaseEntity {
    /**
     * 所属林地
     */
    @ManyToOne
    private Woodland woodland;
    /**
     * 创建人
     */
    @ManyToOne
    private User creator;
    /**
     * 树木总数
     */
    @Column(nullable=false)
    private Integer treeCount;
    /**
     * 最大树高
     */
    @Column(nullable=false)
    private Integer maxHeight;
    /**
     * 最小树高
     */
    @Column(nullable=false)
    private Integer minHeight;
    /**
     * 平均树高
     */
    @Column(nullable=false)
    private Integer meanHeight;
    /**
     * 测量时间
     */
    @Column(nullable=false)
    private Date measureTime;
    /**
     * 附加信息(JSON)
     */
    @Column(length=512)
    private String addition;
    /**
     * 树木是否携带编号
     */
    @Column(nullable = false)
    private Boolean isTreeWithId;

    /**
     * 此记录下的单个树记录
     */
    @OneToMany(mappedBy = "record")
    private List<Tree> trees = new ArrayList<>();
}
