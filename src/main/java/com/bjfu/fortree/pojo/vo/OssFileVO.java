package com.bjfu.fortree.pojo.vo;

import com.bjfu.fortree.pojo.dto.OssFileDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class OssFileVO {

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
     * 文件过期时间
     */
    private Date expiresTime;
    /**
     * 文件下载URL
     */
    private String url;

    public OssFileVO(OssFileDTO ossFileDTO) {
        if (ossFileDTO != null) {
            BeanUtils.copyProperties(ossFileDTO, this);
        }
    }
}
