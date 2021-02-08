package com.bjfu.fortree.service.impl;

import com.bjfu.fortree.approval.ApprovedOperationDispatch;
import com.bjfu.fortree.dto.job.ApplyJobDTO;
import com.bjfu.fortree.dto.user.UserDTO;
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
import com.bjfu.fortree.request.apply.GetAllApplyJobRequest;
import com.bjfu.fortree.request.apply.GetMyApplyJobRequest;
import com.bjfu.fortree.service.ApplyJobService;
import com.bjfu.fortree.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
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
    @Autowired
    private ApprovedOperationDispatch approvedOperationDispatch;

    @Override
    public PageVO<ApplyJobDTO> getAllApplyJob(GetAllApplyJobRequest getAllApplyJobRequest, String userAccount) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        if(!user.getType().equals(UserTypeEnum.ADMIN)) {
            throw new UnauthorizedOperationException(ResultEnum.REQUIRE_ADMIN);
        }
        PageRequest pageRequest = PageRequest.of(getAllApplyJobRequest.getCurrent() - 1, getAllApplyJobRequest.getPageSize());
        if(getAllApplyJobRequest.getField() != null) {
            Sort sort = Sort.by(new Sort.Order(getAllApplyJobRequest.getOrder(), getAllApplyJobRequest.getField()));
            pageRequest = PageRequest.of(getAllApplyJobRequest.getCurrent() - 1, getAllApplyJobRequest.getPageSize(), sort);
        }
        Page<ApplyJob> applyJobs = applyJobRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!CollectionUtils.isEmpty(getAllApplyJobRequest.getState())) {
                predicates.add(cb.and(root.get("state").in(getAllApplyJobRequest.getState())));
            }
            if (!CollectionUtils.isEmpty(getAllApplyJobRequest.getType())) {
                predicates.add(cb.and(root.get("type").in(getAllApplyJobRequest.getType())));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        List<ApplyJobDTO> applyJobDTOList = applyJobs.getContent().stream()
                .map(ApplyJobDTO::new)
                .collect(Collectors.toList());
        return new PageVO<>(applyJobs.getTotalElements(), applyJobDTOList);
    }

    @Override
    public ApplyJobDTO getApplyJobDetail(Long applyJobId, String userAccount) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        Optional<ApplyJob> applyJobOptional = applyJobRepository.findById(applyJobId);
        if(applyJobOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.APPLYJOB_NOT_EXIST);
        }
        ApplyJob applyJob = applyJobOptional.get();
        if(!user.getType().equals(UserTypeEnum.ADMIN) &&
                !applyJob.getApplyUser().getAccount().equals(userAccount)) {
            throw new UnauthorizedOperationException(ResultEnum.PERMISSION_DENIED);
        }
        return new ApplyJobDTO(applyJob);
    }

    @Override
    @Transactional
    public ApplyJobDTO approvalApplyJob(String userAccount, ApprovalApplyJobRequest approvalApplyJobRequest) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
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
        if(approvalApplyJobRequest.getState().equals(ApplyJobStateEnum.PASSED)) {
            approvedOperationDispatch.dispatch(applyJob);
        }
        applyJob = applyJobRepository.save(applyJob);
        return new ApplyJobDTO(applyJob);
    }

    @Override
    public PageVO<ApplyJobDTO> getApplyJobByApplyUser(GetMyApplyJobRequest getMyApplyJobRequest, String userAccount) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        PageRequest pageRequest = PageRequest.of(getMyApplyJobRequest.getCurrent() - 1, getMyApplyJobRequest.getPageSize());
        if(getMyApplyJobRequest.getField() != null) {
            Sort sort = Sort.by(new Sort.Order(getMyApplyJobRequest.getOrder(), getMyApplyJobRequest.getField()));
            pageRequest = PageRequest.of(getMyApplyJobRequest.getCurrent() - 1, getMyApplyJobRequest.getPageSize(), sort);
        }
        Page<ApplyJob> applyJobs = applyJobRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!CollectionUtils.isEmpty(getMyApplyJobRequest.getState())) {
                predicates.add(cb.and(root.get("state").in(getMyApplyJobRequest.getState())));
            }
            if (!CollectionUtils.isEmpty(getMyApplyJobRequest.getType())) {
                predicates.add(cb.and(root.get("type").in(getMyApplyJobRequest.getType())));
            }
            predicates.add(cb.equal(root.get("applyUser"), user));
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        List<ApplyJobDTO> applyJobDTOList = applyJobs.getContent().stream()
                .map(ApplyJobDTO::new)
                .collect(Collectors.toList());
        return new PageVO<>(applyJobs.getTotalElements(), applyJobDTOList);
    }

    @Override
    @Transactional
    public ApplyJobDTO cancelApplyJob(Long applyJobId, String userAccount) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        Optional<ApplyJob> applyJobOptional = applyJobRepository.findByIdForUpdate(applyJobId);
        if(applyJobOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.APPLYJOB_NOT_EXIST);
        }
        ApplyJob applyJob = applyJobOptional.get();
        if(!applyJob.getApplyUser().getAccount().equals(userAccount)) {
            throw new UnauthorizedOperationException(ResultEnum.NOT_APPLY_USER);
        }
        if(!applyJob.getState().equals(ApplyJobStateEnum.APPLYING)) {
            throw new NotAllowedOperationException(ResultEnum.APPLYJOB_STATE_CHANGE_NOT_ALLOWED);
        }
        applyJob.setState(ApplyJobStateEnum.CANCELLED);
        applyJob.setOperateUser(user);
        applyJob.setMsg("申请人撤销申请");
        applyJob.setOperateTime(new Date());
        applyJob = applyJobRepository.save(applyJob);
        return new ApplyJobDTO(applyJob);
    }
}
