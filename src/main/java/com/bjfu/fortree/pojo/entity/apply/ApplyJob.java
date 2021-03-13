package com.bjfu.fortree.pojo.entity.apply;

import com.bjfu.fortree.pojo.entity.BaseEntity;
import com.bjfu.fortree.pojo.entity.file.OssFile;
import com.bjfu.fortree.pojo.entity.user.User;
import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 申请实体类
 * @author warthog
 */
@Getter
@Setter
@Entity
@Table(name = "fortree_applyjob")
public class ApplyJob extends BaseEntity {
    /**
     * 申请人
     */
    @ManyToOne
    private User applyUser;

    /**
     * 申请类型
     */
    @Enumerated
    @Column(nullable = false)
    private ApplyJobTypeEnum type;
    /**
     * 申请参数
     */
    @Column(length=512, nullable=false)
    private String applyParam;
    /**
     * 申请时上传的oss文件
     */
    @OneToOne
    private OssFile uploadFile;
    /**
     * 下载的oss文件
     */
    @OneToOne
    private OssFile downloadFile;
    /**
     * 申请状态
     */
    @Enumerated
    @Column(nullable = false)
    private ApplyJobStateEnum state;

    /**
     * 状态变更操作人
     */
    @ManyToOne
    private User operateUser;
    /**
     * 状态变更信息
     */
    @Column(length=256)
    private String msg;
    /**
     * 状态变更时间
     */
    @Column(nullable = false)
    private Date operateTime;

    public static ApplyJob createApply(User applyUser, ApplyJobTypeEnum type, String applyParam) {
        return createApply(applyUser, type, applyParam, "申请中");
    }

    public static ApplyJob createApply(User applyUser, ApplyJobTypeEnum type, String applyParam, String msg) {
        ApplyJob applyJob = new ApplyJob();
        applyJob.setApplyUser(applyUser);
        applyJob.setType(type);
        applyJob.setApplyParam(applyParam);
        applyJob.setState(ApplyJobStateEnum.APPLYING);
        applyJob.setOperateUser(applyUser);
        applyJob.setMsg(msg);
        applyJob.setOperateTime(new Date());
        return applyJob;
    }

    public static ApplyJob createPassedApply(User applyUser, ApplyJobTypeEnum type, String applyParam) {
        return createPassedApply(applyUser, type, applyParam, "拥有权限自动审批");
    }

    public static ApplyJob createPassedApply(User applyUser, ApplyJobTypeEnum type, String applyParam, String msg) {
        ApplyJob applyJob = new ApplyJob();
        applyJob.setApplyUser(applyUser);
        applyJob.setType(type);
        applyJob.setApplyParam(applyParam);
        applyJob.setState(ApplyJobStateEnum.PASSED);
        applyJob.setOperateUser(applyUser);
        applyJob.setMsg(msg);
        applyJob.setOperateTime(new Date());
        return applyJob;
    }

}
