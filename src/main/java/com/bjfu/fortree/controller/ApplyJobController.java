package com.bjfu.fortree.controller;

import com.bjfu.fortree.dto.job.ApplyJobDTO;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.request.apply.ApprovalApplyJobRequest;
import com.bjfu.fortree.request.apply.GetAllApplyJobRequest;
import com.bjfu.fortree.request.apply.GetMyApplyJobRequest;
import com.bjfu.fortree.service.ApplyJobService;
import com.bjfu.fortree.util.SessionUtil;
import com.bjfu.fortree.vo.BaseResult;
import com.bjfu.fortree.vo.PageVO;
import com.bjfu.fortree.vo.apply.ApplyJobVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/getAllApplyJob")
    public BaseResult<PageVO<ApplyJobVO>> getAllApplyJob(@Validated @RequestBody GetAllApplyJobRequest getAllApplyJobRequest,
                                                       HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        PageVO<ApplyJobDTO> applyJobDTOS = applyJobService.getAllApplyJob(getAllApplyJobRequest, userAccount);
        List<ApplyJobVO> applyJobVOS = applyJobDTOS.getContents().stream().map(ApplyJobVO::new).collect(Collectors.toList());
        return new BaseResult<>(ResultEnum.SUCCESS, new PageVO<>(applyJobDTOS.getCount(), applyJobVOS));
    }

    @GetMapping("/getApplyJobDetail")
    public BaseResult<ApplyJobVO> getApplyJobDetail(@NotNull(message = "申请id不能为空!") Long id, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDetail = applyJobService.getApplyJobDetail(id, userAccount);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDetail));
    }

    @PostMapping("/approvalApplyJob")
    public BaseResult<ApplyJobVO> approvalApplyJob(@Validated @RequestBody ApprovalApplyJobRequest approvalApplyJobRequest, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = applyJobService.approvalApplyJob(userAccount, approvalApplyJobRequest);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @PostMapping("/getMyApplyJob")
    public BaseResult<PageVO<ApplyJobVO>> getMyApplyJob(@Validated @RequestBody GetMyApplyJobRequest getMyApplyJobRequest, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        PageVO<ApplyJobDTO> applyJobDTOPageVO = applyJobService.getApplyJobByApplyUser(getMyApplyJobRequest, userAccount);
        List<ApplyJobVO> applyJobVOList = applyJobDTOPageVO.getContents().stream()
                .map(ApplyJobVO::new)
                .collect(Collectors.toList());
        return new BaseResult<>(ResultEnum.SUCCESS, new PageVO<>(applyJobDTOPageVO.getCount(), applyJobVOList));
    }

    @PostMapping("/cancelApplyJob")
    public BaseResult<ApplyJobVO> cancelApplyJob(@NotNull(message = "申请id不能为空!") Long id, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = applyJobService.cancelApplyJob(id, userAccount);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

}
