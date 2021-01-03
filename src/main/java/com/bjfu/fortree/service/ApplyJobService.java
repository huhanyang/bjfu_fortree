package com.bjfu.fortree.service;

import com.bjfu.fortree.dto.job.ApplyJobDTO;
import com.bjfu.fortree.request.apply.ApprovalApplyJobRequest;

import java.util.List;

/**
 * 审批相关
 * @author warthog
 */
public interface ApplyJobService {
    /**
     * 获取所有的申请列表
     * @param userAccount 用户账号
     * @return 申请列表
     */
    List<ApplyJobDTO> getApplyJob(String userAccount);

    /**
     * 审批此申请
     * @param userAccount 用户账户
     * @param approvalApplyJobRequest 请求
     * @return 申请实体
     */
    ApplyJobDTO approvalApplyJob(String userAccount, ApprovalApplyJobRequest approvalApplyJobRequest);

    /**
     * 获取用户创建的申请列表
     * @param userAccount 用户账号
     * @return 申请列表
     */
    List<ApplyJobDTO> getApplyJobByApplyUser(String userAccount);
}
