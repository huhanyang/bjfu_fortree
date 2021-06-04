package com.bjfu.fortree.pojo.dto;

import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.Optional;

@Data
public class ApplyJobDTO {

    public ApplyJobDTO(ApplyJob applyJob, Boolean needApplyUser, Boolean needUploadFile, Boolean needDownloadFile, Boolean needOperateUser) {
        if(applyJob != null) {
            BeanUtils.copyProperties(applyJob, this, "applyUser", "uploadFile", "downloadFile", "operateUser");
            if(needApplyUser) {
                this.applyUser = new UserDTO(applyJob.getApplyUser(), false, false, false, false);
            }
            if(needUploadFile) {
                this.uploadFile = Optional.ofNullable(applyJob.getUploadFile()).map(OssFileDTO::new).orElse(null);
            }
            if(needDownloadFile) {
                this.downloadFile = Optional.ofNullable(applyJob.getDownloadFile()).map(OssFileDTO::new).orElse(null);
            }
            if(needOperateUser) {
                this.operateUser = new UserDTO(applyJob.getOperateUser(), false, false, false, false);
            }
        }
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
     * 申请人
     */
    private UserDTO applyUser;

    /**
     * 申请类型
     */
    private ApplyJobTypeEnum type;
    /**
     * 申请参数
     */
    private String applyParam;
    /**
     * 申请时上传的oss文件
     */
    private OssFileDTO uploadFile;
    /**
     * 下载的oss文件
     */
    private OssFileDTO downloadFile;
    /**
     * 申请状态
     */
    private ApplyJobStateEnum state;

    /**
     * 状态变更操作人
     */
    private UserDTO operateUser;
    /**
     * 状态变更信息
     */
    private String msg;
    /**
     * 状态变更时间
     */
    private Date operateTime;
}
