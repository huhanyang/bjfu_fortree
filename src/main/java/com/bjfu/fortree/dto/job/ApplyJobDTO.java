package com.bjfu.fortree.dto.job;

import com.bjfu.fortree.dto.user.UserDTO;
import com.bjfu.fortree.entity.apply.ApplyJob;
import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import java.util.Date;

@Data
public class ApplyJobDTO {
    public ApplyJobDTO() {}
    public ApplyJobDTO(ApplyJob applyJob) {
        BeanUtils.copyProperties(applyJob, this);
        this.setApplyUser(new UserDTO(applyJob.getApplyUser()));
        if(applyJob.getOperateUser() != null) {
            this.setOperateUser(new UserDTO(applyJob.getOperateUser()));
        }
    }
    /**
     * 主键
     */
    private Long id;
    /**
     * 申请类型
     */
    private ApplyJobTypeEnum type;
    /**
     * 申请参数
     */
    private String applyParam;
    /**
     * 申请状态
     */
    private ApplyJobStateEnum state;
    /**
     * 申请人
     */
    private UserDTO applyUser;
    /**
     * 状态操作人
     */
    private UserDTO operateUser;
    /**
     * 状态变更信息
     */
    private String msg;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 申请通过/未通过的时间
     */
    private Date operateTime;
}
