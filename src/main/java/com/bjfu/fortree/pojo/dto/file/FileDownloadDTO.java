package com.bjfu.fortree.pojo.dto.file;

import com.bjfu.fortree.pojo.entity.file.OssFile;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author warthog
 */
@Data
public class FileDownloadDTO {
    public FileDownloadDTO() {}
    public FileDownloadDTO(OssFile ossFile) {
        BeanUtils.copyProperties(ossFile, this);
    }

    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件过期时间
     */
    private Date expiresTime;
    /**
     * 文件下载链接
     */
    private String downloadUrl;
    /**
     * 文件下载链接超时时间
     */
    private Date downloadUrlExpiresTime;
}
