package com.bjfu.fortree.pojo.entity;

import com.bjfu.fortree.enums.entity.RecordTypeEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 林地记录实体类
 *
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
    @Column(nullable = false)
    private Integer treeCount;
    /**
     * 最大树高 厘米
     */
    @Column(nullable = false)
    private Double maxHeight;
    /**
     * 最小树高 厘米
     */
    @Column(nullable = false)
    private Double minHeight;
    /**
     * 平均树高 厘米
     */
    @Column(nullable = false)
    private Double meanHeight;
    /**
     * 测量时间
     */
    @Column(nullable = false)
    private Date measureTime;
    /**
     * 附加信息(JSON)
     */
    @Column(length = 512)
    private String addition;
    /**
     * 树木是否携带编号
     */
    @Column(nullable = false)
    private Boolean isTreeWithId;
    /**
     * 类型
     */
    @Enumerated
    @Column(nullable = false)
    private RecordTypeEnum type;
    /**
     * 树高测量方式
     */
    @Column(nullable = false, length = 16)
    private String measureType;
    /**
     * 郁闭度 0-100(%)
     */
    @Column(nullable = false)
    private Double canopyDensity;
    /**
     * 优势树种
     */
    @Column(nullable = false, length = 32)
    private String dominantSpecies;

    /**
     * 龄组
     */
    @Column(length = 64)
    private String ageGroup;
    /**
     * 坡度
     */
    @Column(length = 64)
    private String slope;
    /**
     * 坡向
     */
    @Column(length = 64)
    private String aspect;
    /**
     * 起源
     */
    @Column(length = 64)
    private String origin;
    /**
     * 树种组成
     */
    @Column(length = 256)
    private String speciesComposition;

    /**
     * 此记录下的单个树记录
     */
    @OneToMany(mappedBy = "record")
    private List<Tree> trees = new ArrayList<>();
}
