package com.bjfu.fortree.pojo.entity;

import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体类
 * @author warthog
 */
@Getter
@Setter
@Entity
@Table(name = "fortree_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"account"})})
public class User extends BaseEntity {
    /**
     * 用户名
     */
    @Column(length=32, nullable=false)
    private String account;
    /**
     * 密码
     */
    @Column(length=32, nullable=false)
    private String password;
    /**
     * 姓名
     */
    @Column(length=32, nullable=false)
    private String name;
    /**
     * 所属组织名
     */
    @Column(length=32, nullable=false)
    private String organization;
    /**
     * 账号类型
     */
    @Enumerated
    @Column(nullable=false)
    private UserTypeEnum type;

    /**
     * 账号状态
     */
    @Enumerated
    @Column(nullable=false)
    private UserStateEnum state;

    /**
     * 拥有的权限
     */
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "user")
    private List<Authority> authorities = new ArrayList<>();

    /**
     * 创建的林地
     */
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "creator")
    private List<Woodland> woodlands = new ArrayList<>();

    /**
     * 创建的记录
     */
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "creator")
    private List<Record> records = new ArrayList<>();

    /**
     * 申请
     */
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "applyUser")
    private List<ApplyJob> applyJobs = new ArrayList<>();


}
