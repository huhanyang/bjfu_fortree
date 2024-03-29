package com.bjfu.fortree.controller;

import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.exception.WrongParamException;
import com.bjfu.fortree.pojo.BaseResult;
import com.bjfu.fortree.pojo.dto.ApplyJobDTO;
import com.bjfu.fortree.pojo.dto.OssFileDTO;
import com.bjfu.fortree.pojo.dto.UserDTO;
import com.bjfu.fortree.pojo.request.apply.ApprovalApplyJobRequest;
import com.bjfu.fortree.pojo.request.apply.GetApplyJobsRequest;
import com.bjfu.fortree.pojo.vo.ApplyJobVO;
import com.bjfu.fortree.pojo.vo.OssFileVO;
import com.bjfu.fortree.security.annotation.RequireAdmin;
import com.bjfu.fortree.security.annotation.RequireLogin;
import com.bjfu.fortree.service.ApplyJobService;
import com.bjfu.fortree.util.UserInfoContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 申请相关操作接口
 *
 * @author warthog
 */
@Validated
@RestController
@RequestMapping("/applyJob")
public class ApplyJobController {

    @Autowired
    private ApplyJobService applyJobService;

    @RequireAdmin
    @PostMapping("/getApplyJobs")
    public BaseResult<Page<ApplyJobVO>> getApplyJobs(@Validated @RequestBody GetApplyJobsRequest request) {
        Page<ApplyJobDTO> applyJobs = applyJobService.getApplyJobs(request);
        return new BaseResult<>(ResultEnum.SUCCESS, applyJobs.map(ApplyJobVO::new));
    }

    @RequireLogin
    @GetMapping("/getApplyJobDetail")
    public BaseResult<ApplyJobVO> getApplyJobDetail(@NotNull(message = "申请id不能为空!") Long id) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        ApplyJobDTO applyJobDetail = applyJobService.getApplyJobDetail(id, userInfo.getAccount());
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDetail));
    }

    @RequireLogin
    @PostMapping("/getMyApplyJobs")
    public BaseResult<Page<ApplyJobVO>> getMyApplyJobs(@Validated @RequestBody GetApplyJobsRequest request) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        Page<ApplyJobDTO> applyJobs = applyJobService.getApplyJobsByApplyUser(request, userInfo.getAccount());
        return new BaseResult<>(ResultEnum.SUCCESS, applyJobs.map(ApplyJobVO::new));
    }

    @RequireAdmin
    @PostMapping("/approvalApplyJob")
    public BaseResult<ApplyJobVO> approvalApplyJob(@Validated @RequestBody ApprovalApplyJobRequest request) {
        // 参数校验
        if (!request.getState().equals(ApplyJobStateEnum.PASSED) && !request.getState().equals(ApplyJobStateEnum.NOT_PASSED)) {
            throw new WrongParamException(ResultEnum.PARAM_WRONG);
        }
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        ApplyJobDTO applyJobDTO = applyJobService.approvalApplyJob(request, userInfo.getAccount());
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @PostMapping("/cancelApplyJob")
    public BaseResult<ApplyJobVO> cancelApplyJob(@NotNull(message = "申请id不能为空!") Long id) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        ApplyJobDTO applyJobDTO = applyJobService.cancelApplyJob(id, userInfo.getAccount());
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @GetMapping("/getApplyJobDownloadFileInfo")
    public BaseResult<OssFileVO> getApplyJobDownloadFileInfo(@NotNull(message = "申请id不能为空!") Long id,
                                                             @NotNull(message = "是否为上传文件不能为空") Boolean isUploadFile) {
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        OssFileDTO ossFileDTO = applyJobService.getApplyJobDownloadFileInfo(id, isUploadFile, userInfo.getAccount());
        return new BaseResult<>(ResultEnum.SUCCESS, new OssFileVO(ossFileDTO));
    }

}
