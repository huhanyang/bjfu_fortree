package com.bjfu.fortree.pojo.vo.file;

import com.bjfu.fortree.pojo.dto.file.FileDownloadDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author warthog
 */
@Data
public class FileDownloadVO {
    public FileDownloadVO() {}
    public FileDownloadVO(FileDownloadDTO fileDownloadDTO) {
        BeanUtils.copyProperties(fileDownloadDTO, this);
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
