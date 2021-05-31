package com.bjfu.fortree.service;

import com.bjfu.fortree.pojo.dto.file.FileDownloadDTO;
import com.bjfu.fortree.pojo.dto.job.ApplyJobDTO;
import com.bjfu.fortree.pojo.request.apply.ApprovalApplyJobRequest;
import com.bjfu.fortree.pojo.request.apply.GetAllApplyJobRequest;
import com.bjfu.fortree.pojo.request.apply.GetMyApplyJobRequest;
import org.springframework.data.domain.Page;

/**
 * 审批相关
 * @author warthog
 */
public interface ApplyJobService {
    /**
     * 获取所有的申请列表
     * @param request 请求
     * @return 申请列表
     */
    Page<ApplyJobDTO> getAllApplyJob(GetAllApplyJobRequest request);

    /**
     * 获取申请详情
     * @param applyJobId 申请的id
     * @param userAccount 用户账号
     * @return 申请详情
     */
    ApplyJobDTO getApplyJobDetail(Long applyJobId, String userAccount);

    /**
     * 审批此申请
     * @param request 请求
     * @param userAccount 用户账户
     * @return 申请实体
     */
    ApplyJobDTO approvalApplyJob(ApprovalApplyJobRequest request, String userAccount);

    /**
     * 获取用户创建的申请列表
     * @param request 请求
     * @param userAccount 用户账号
     * @return 申请列表
     */
    Page<ApplyJobDTO> getApplyJobByApplyUser(GetMyApplyJobRequest request, String userAccount);

    /**
     * 取消申请
     * @param applyJobId 申请id
     * @param userAccount 操作者用户账号
     * @return 申请实体
     */
    ApplyJobDTO cancelApplyJob(Long applyJobId, String userAccount);

    /**
     * 获取文件下载链接
     * @param applyJobId 申请id
     * @param userAccount 操作者用户账号
     * @return 申请实体
     */
    FileDownloadDTO getApplyJobDownloadFileUrl(Long applyJobId, String userAccount);
}
