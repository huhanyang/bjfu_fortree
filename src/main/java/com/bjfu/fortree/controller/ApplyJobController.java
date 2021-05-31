package com.bjfu.fortree.controller;

import com.bjfu.fortree.pojo.dto.file.FileDownloadDTO;
import com.bjfu.fortree.pojo.dto.job.ApplyJobDTO;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.pojo.request.apply.ApprovalApplyJobRequest;
import com.bjfu.fortree.pojo.request.apply.GetAllApplyJobRequest;
import com.bjfu.fortree.pojo.request.apply.GetMyApplyJobRequest;
import com.bjfu.fortree.security.annotation.RequireAdmin;
import com.bjfu.fortree.security.annotation.RequireLogin;
import com.bjfu.fortree.service.ApplyJobService;
import com.bjfu.fortree.pojo.vo.BaseResult;
import com.bjfu.fortree.pojo.vo.PageVO;
import com.bjfu.fortree.pojo.vo.apply.ApplyJobVO;
import com.bjfu.fortree.pojo.vo.file.FileDownloadVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 申请相关操作接口
 * @author warthog
 */
@RestController
@RequestMapping("/applyJob")
@Validated
public class ApplyJobController {

    @Autowired
    private ApplyJobService applyJobService;

    @RequireLogin
    @PostMapping("/getAllApplyJob")
    public BaseResult<PageVO<ApplyJobVO>> getAllApplyJob(@Validated @RequestBody GetAllApplyJobRequest getAllApplyJobRequest,
                                                       HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        PageVO<ApplyJobDTO> applyJobDTOS = applyJobService.getAllApplyJob(getAllApplyJobRequest, userAccount);
        List<ApplyJobVO> applyJobVOS = applyJobDTOS.getContents().stream().map(ApplyJobVO::new).collect(Collectors.toList());
        return new BaseResult<>(ResultEnum.SUCCESS, new PageVO<>(applyJobDTOS.getCount(), applyJobVOS));
    }

    @RequireLogin
    @GetMapping("/getApplyJobDetail")
    public BaseResult<ApplyJobVO> getApplyJobDetail(@NotNull(message = "申请id不能为空!") Long id, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDetail = applyJobService.getApplyJobDetail(id, userAccount);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDetail));
    }

    @RequireAdmin
    @PostMapping("/approvalApplyJob")
    public BaseResult<ApplyJobVO> approvalApplyJob(@Validated @RequestBody ApprovalApplyJobRequest approvalApplyJobRequest,
                                                   HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = applyJobService.approvalApplyJob(userAccount, approvalApplyJobRequest);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @PostMapping("/getMyApplyJob")
    public BaseResult<PageVO<ApplyJobVO>> getMyApplyJob(@Validated @RequestBody GetMyApplyJobRequest getMyApplyJobRequest,
                                                        HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        PageVO<ApplyJobDTO> applyJobDTOPageVO = applyJobService.getApplyJobByApplyUser(getMyApplyJobRequest, userAccount);
        List<ApplyJobVO> applyJobVOList = applyJobDTOPageVO.getContents().stream()
                .map(ApplyJobVO::new)
                .collect(Collectors.toList());
        return new BaseResult<>(ResultEnum.SUCCESS, new PageVO<>(applyJobDTOPageVO.getCount(), applyJobVOList));
    }

    @RequireLogin
    @PostMapping("/cancelApplyJob")
    public BaseResult<ApplyJobVO> cancelApplyJob(@NotNull(message = "申请id不能为空!") Long id, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = applyJobService.cancelApplyJob(id, userAccount);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @RequireLogin
    @GetMapping("/getApplyJobDownloadFileUrl")
    public BaseResult<FileDownloadVO> getApplyJobDownloadFileUrl(@NotNull(message = "申请id不能为空!") Long id, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        FileDownloadDTO fileDownloadDTO = applyJobService.getApplyJobDownloadFileUrl(id, userAccount);
        return new BaseResult<>(ResultEnum.SUCCESS, new FileDownloadVO(fileDownloadDTO));
    }

}
