package com.bjfu.fortree.pojo.vo;

import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import com.bjfu.fortree.pojo.dto.ApplyJobDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.Optional;

@Data
public class ApplyJobVO {

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
    private UserVO applyUser;
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
    private OssFileVO uploadFile;
    /**
     * 下载的oss文件
     */
    private OssFileVO downloadFile;
    /**
     * 申请状态
     */
    private ApplyJobStateEnum state;
    /**
     * 状态变更操作人
     */
    private UserVO operateUser;
    /**
     * 状态变更信息
     */
    private String msg;
    /**
     * 状态变更时间
     */
    private Date operateTime;

    public ApplyJobVO(ApplyJobDTO applyJobDTO) {
        if (applyJobDTO != null) {
            BeanUtils.copyProperties(applyJobDTO, this);
            this.applyUser = Optional.ofNullable(applyJobDTO.getApplyUser()).map(UserVO::new).orElse(null);
            this.operateUser = Optional.ofNullable(applyJobDTO.getOperateUser()).map(UserVO::new).orElse(null);
            this.uploadFile = Optional.ofNullable(applyJobDTO.getUploadFile()).map(OssFileVO::new).orElse(null);
            this.downloadFile = Optional.ofNullable(applyJobDTO.getDownloadFile()).map(OssFileVO::new).orElse(null);
        }
    }
}
