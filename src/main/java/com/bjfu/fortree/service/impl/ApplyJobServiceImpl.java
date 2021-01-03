package com.bjfu.fortree.service.impl;

import com.bjfu.fortree.dto.job.ApplyJobDTO;
import com.bjfu.fortree.entity.apply.ApplyJob;
import com.bjfu.fortree.entity.user.User;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.exception.NotAllowedOperationException;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.exception.UnauthorizedOperationException;
import com.bjfu.fortree.exception.WrongParamException;
import com.bjfu.fortree.repository.job.ApplyJobRepository;
import com.bjfu.fortree.repository.user.AuthorityRepository;
import com.bjfu.fortree.repository.user.UserRepository;
import com.bjfu.fortree.request.apply.ApprovalApplyJobRequest;
import com.bjfu.fortree.service.ApplyJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author warthog
 */
@Service
public class ApplyJobServiceImpl implements ApplyJobService {

    @Autowired
    private ApplyJobRepository applyJobRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ApplyJobDTO> getApplyJob(String userAccount) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.SESSION_USER_NOT_EXIST);
        }
        User user = userOptional.get();
        if(!user.getType().equals(UserTypeEnum.ADMIN)) {
            throw new UnauthorizedOperationException(ResultEnum.REQUIRE_ADMIN);
        }
        List<ApplyJob> applyJobs = applyJobRepository.findAll();
        return applyJobs.stream().map(ApplyJobDTO::new).collect(Collectors.toList());
    }

    @Override
    public ApplyJobDTO approvalApplyJob(String userAccount, ApprovalApplyJobRequest approvalApplyJobRequest) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.SESSION_USER_NOT_EXIST);
        }
        User user = userOptional.get();
        if(!user.getType().equals(UserTypeEnum.ADMIN)) {
            throw new UnauthorizedOperationException(ResultEnum.REQUIRE_ADMIN);
        }
        Optional<ApplyJob> applyJobOptional = applyJobRepository.findByIdForUpdate(approvalApplyJobRequest.getApplyJobId());
        if(applyJobOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.APPLYJOB_NOT_EXIST);
        }
        ApplyJob applyJob = applyJobOptional.get();
        if(!ApplyJobStateEnum.APPLYING.equals(applyJob.getState())) {
            throw new NotAllowedOperationException(ResultEnum.APPLYJOB_STATE_CHANGE_NOT_ALLOWED);
        }
        applyJob.setState(approvalApplyJobRequest.getState());
        applyJob.setOperateUser(user);
        applyJob.setOperateTime(new Date());
        applyJob.setMsg(approvalApplyJobRequest.getMsg());
        applyJob = applyJobRepository.save(applyJob);
        return new ApplyJobDTO(applyJob);
    }

    @Override
    public List<ApplyJobDTO> getApplyJobByApplyUser(String userAccount) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.SESSION_USER_NOT_EXIST);
        }
        User user = userOptional.get();
        List<ApplyJob> applyJobs = applyJobRepository.findByApplyUser(user);
        return applyJobs.stream().map(ApplyJobDTO::new).collect(Collectors.toList());
    }
}
