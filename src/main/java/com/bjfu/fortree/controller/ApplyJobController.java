package com.bjfu.fortree.controller;

import com.bjfu.fortree.dto.job.ApplyJobDTO;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.request.apply.ApprovalApplyJobRequest;
import com.bjfu.fortree.service.ApplyJobService;
import com.bjfu.fortree.util.SessionUtil;
import com.bjfu.fortree.vo.BaseResult;
import com.bjfu.fortree.vo.apply.ApplyJobVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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

    @GetMapping("/loginCheck")
    public BaseResult<List<ApplyJobVO>> getApplyJob(HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        List<ApplyJobDTO> applyJobDTOS = applyJobService.getApplyJob(userAccount);
        List<ApplyJobVO> applyJobVOS = applyJobDTOS.stream().map(ApplyJobVO::new).collect(Collectors.toList());
        return new BaseResult<>(ResultEnum.SUCCESS, applyJobVOS);
    }

    @PostMapping("/approvalApplyJob")
    public BaseResult<ApplyJobVO> approvalApplyJob(@Validated @RequestBody ApprovalApplyJobRequest approvalApplyJobRequest, HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        ApplyJobDTO applyJobDTO = applyJobService.approvalApplyJob(userAccount, approvalApplyJobRequest);
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }

    @GetMapping("/getApplyJobByApplyUser")
    public BaseResult<List<ApplyJobVO>> getApplyJobByApplyUser(HttpSession session) {
        String userAccount = SessionUtil.getUserInfo(session).getAccount();
        List<ApplyJobDTO> applyJobDTOS = applyJobService.getApplyJobByApplyUser(userAccount);
        List<ApplyJobVO> applyJobVOS = applyJobDTOS.stream().map(ApplyJobVO::new).collect(Collectors.toList());
        return new BaseResult<>(ResultEnum.SUCCESS, applyJobVOS);
    }

}
