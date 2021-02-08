package com.bjfu.fortree.entity.woodland;

import com.bjfu.fortree.entity.BaseEntity;
import com.bjfu.fortree.entity.user.User;
import com.bjfu.fortree.enums.entity.WoodlandShapeEnum;
import lombok.Getter;
import lombok.Setter;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 林地实体类
 * @author warthog
 */
@Getter
@Setter
@Entity
@Table(name = "fortree_woodland")
public class Woodland extends BaseEntity {
    /**
     * 样地名称
     */
    @Column(length=32, nullable=false)
    private String name;
    /**
     * 创建人
     */
    @ManyToOne
    private User creator;
    /**
     * 样地地址
     */
    @Column(length=64, nullable=false)
    private String address;
    /**
     * 样地中心经纬度
     */
    @Column(nullable=false)
    private Point<G2D> position;
    /**
     * 国家
     */
    @Column(length=32, nullable=false)
    private String country;
    /**
     * 省/州
     */
    @Column(length=32, nullable=false)
    private String province;
    /**
     * 城市
     */
    @Column(length=32, nullable=false)
    private String city;
    /**
     * 形状
     */
    @Enumerated
    @Column(nullable=false)
    private WoodlandShapeEnum shape;
    /**
     * 长度 米
     */
    @Column(nullable=false)
    private Double length;
    /**
     * 宽度 米
     */
    @Column(nullable=false)
    private Double width;
    /**
     * 附加信息(JSON)
     */
    @Column(length=512)
    private String addition;

    /**
     * 创建的记录
     */
    @OneToMany(mappedBy = "woodland")
    @OrderBy("measureTime")
    private List<Record> records = new ArrayList<>();
}
