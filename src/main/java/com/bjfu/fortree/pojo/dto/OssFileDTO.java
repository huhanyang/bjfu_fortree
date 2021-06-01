package com.bjfu.fortree.pojo.dto;

import com.bjfu.fortree.pojo.entity.OssFile;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class OssFileDTO {

    public OssFileDTO(OssFile ossFile) {
        BeanUtils.copyProperties(ossFile, this);
    }
    /**
     * 主键
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 修改时间
     */
    private Date lastModifiedTime;
    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件所在oss里的bucket
     */
    private String ossBucketName;
    /**
     * 文件在oss里的object名
     */
    private String ossObjectName;
    /**
     * 文件过期时间
     */
    private Date expiresTime;
}
