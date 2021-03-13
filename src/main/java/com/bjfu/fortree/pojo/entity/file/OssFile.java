package com.bjfu.fortree.pojo.entity.file;

import com.bjfu.fortree.pojo.entity.BaseEntity;
import com.bjfu.fortree.enums.entity.FileTypeEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.Date;

/**
 * 文件实体类
 * @author warthog
 */
@Getter
@Setter
@Entity
@Table(name = "fortree_oss_file")
public class OssFile extends BaseEntity {

    /**
     * 文件名
     */
    @Column(nullable = false, length = 32)
    private String fileName;
    /**
     * 文件类型
     */
    @Enumerated
    @Column(nullable = false)
    private FileTypeEnum type;

    /**
     * 文件所在oss里的bucket
     */
    @Column(nullable = false, length = 32)
    private String ossBucketName;
    /**
     * 文件在oss里的object名
     */
    @Column(nullable = false, length = 64)
    private String ossObjectName;
    /**
     * 文件过期时间
     */
    @Column(nullable = false)
    private Date expiresTime;

    /**
     * 下载用url
     */
    @Column(nullable = true, length = 512)
    private String downloadUrl;
    /**
     * 下载用url过期时间
     */
    @Column(nullable = true)
    private Date downloadUrlExpiresTime;
}
