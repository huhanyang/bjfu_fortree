package com.bjfu.fortree.service.impl;

import com.bjfu.fortree.approval.ApprovedOperationDispatch;
import com.bjfu.fortree.exception.*;
import com.bjfu.fortree.pojo.dto.ApplyJobDTO;
import com.bjfu.fortree.pojo.dto.OssFileDTO;
import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.pojo.entity.OssFile;
import com.bjfu.fortree.pojo.entity.User;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.pojo.request.BasePageAndSorterRequest;
import com.bjfu.fortree.repository.file.OssFileRepository;
import com.bjfu.fortree.repository.job.ApplyJobRepository;
import com.bjfu.fortree.repository.user.UserRepository;
import com.bjfu.fortree.pojo.request.apply.ApprovalApplyJobRequest;
import com.bjfu.fortree.pojo.request.apply.GetApplyJobsRequest;
import com.bjfu.fortree.service.ApplyJobService;
import com.bjfu.fortree.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.*;
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
    private OssService ossService;
    @Autowired
    private ApprovedOperationDispatch approvedOperationDispatch;


    /**
     * 林地实体属性顺序
     */
    private static final Map<String, Integer> APPLYJOB_FIELD_ORDER_WEIGHT = new HashMap<>();
    static {
        APPLYJOB_FIELD_ORDER_WEIGHT.put("type", 1);
        APPLYJOB_FIELD_ORDER_WEIGHT.put("state", 2);
        APPLYJOB_FIELD_ORDER_WEIGHT.put("createdTime", 3);
        APPLYJOB_FIELD_ORDER_WEIGHT.put("operateTime", 4);

    }

    @Override
    public Page<ApplyJobDTO> getApplyJobs(GetApplyJobsRequest request) {
        BasePageAndSorterRequest.Pagination pagination = request.getPagination();
        // 构建多属性排序
        List<Sort.Order> orders = Optional.ofNullable(request.getSorter()).orElse(new LinkedList<>())
                .stream()
                .filter(singleSorter -> StringUtils.hasText(singleSorter.getField()))
                .sorted(Comparator.comparingInt(s -> APPLYJOB_FIELD_ORDER_WEIGHT.get(s.getField())))
                .map(singleSorter -> new Sort.Order(singleSorter.getOrder(), singleSorter.getField()))
                .collect(Collectors.toList());
        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(pagination.getCurrent() - 1, pagination.getPageSize(), Sort.by(orders));
        // 执行查询
        Page<ApplyJob> applyJobs = applyJobRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!CollectionUtils.isEmpty(request.getState())) {
                predicates.add(cb.and(root.get("state").in(request.getState())));
            }
            if (!CollectionUtils.isEmpty(request.getType())) {
                predicates.add(cb.and(root.get("type").in(request.getType())));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        return applyJobs.map(applyJob -> new ApplyJobDTO(applyJob, true, false, true, true));
    }

    @Override
    public ApplyJobDTO getApplyJobDetail(Long applyJobId, String userAccount) {
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        ApplyJob applyJob = applyJobRepository.findById(applyJobId)
                .orElseThrow(() -> new WrongParamException(ResultEnum.APPLYJOB_NOT_EXIST));
        if(!user.getType().equals(UserTypeEnum.ADMIN) &&
                !applyJob.getApplyUser().getAccount().equals(userAccount)) {
            throw new BizException(ResultEnum.PERMISSION_DENIED);
        }
        return new ApplyJobDTO(applyJob, true, true, true, true);
    }

    @Override
    @Transactional
    public ApplyJobDTO approvalApplyJob(ApprovalApplyJobRequest request, String userAccount) {
        // 获取用户信息
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 管理员权限校验
        if(!user.getType().equals(UserTypeEnum.ADMIN)) {
            throw new BizException(ResultEnum.REQUIRE_ADMIN);
        }
        // 获取申请详情并加锁
        ApplyJob applyJob = applyJobRepository.findByIdForUpdate(request.getApplyJobId())
                .orElseThrow(() -> new WrongParamException(ResultEnum.APPLYJOB_NOT_EXIST));
        // 验证申请状态为申请中
        if(!ApplyJobStateEnum.APPLYING.equals(applyJob.getState())) {
            throw new BizException(ResultEnum.APPLYJOB_STATE_CHANGE_NOT_ALLOWED);
        }
        // 修改申请信息
        applyJob.setState(request.getState());
        applyJob.setOperateUser(user);
        applyJob.setOperateTime(new Date());
        applyJob.setMsg(request.getMsg());
        if(request.getState().equals(ApplyJobStateEnum.PASSED)) {
            // 审批通过则执行后续操作
            approvedOperationDispatch.asyncDispatch(applyJob);
        }
        // 落库
        applyJobRepository.save(applyJob);
        return new ApplyJobDTO(applyJob, false, false, false, false);
    }

    @Override
    public Page<ApplyJobDTO> getApplyJobsByApplyUser(GetApplyJobsRequest request, String userAccount) {
        // 获取用户信息
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        BasePageAndSorterRequest.Pagination pagination = request.getPagination();
        // 构建多属性排序
        List<Sort.Order> orders = Optional.ofNullable(request.getSorter()).orElse(new LinkedList<>())
                .stream()
                .filter(singleSorter -> StringUtils.hasText(singleSorter.getField()))
                .sorted(Comparator.comparingInt(s -> APPLYJOB_FIELD_ORDER_WEIGHT.get(s.getField())))
                .map(singleSorter -> new Sort.Order(singleSorter.getOrder(), singleSorter.getField()))
                .collect(Collectors.toList());
        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(pagination.getCurrent() - 1, pagination.getPageSize(), Sort.by(orders));
        // 执行查询
        Page<ApplyJob> applyJobs = applyJobRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("applyUser"), user));
            if (!CollectionUtils.isEmpty(request.getState())) {
                predicates.add(cb.and(root.get("state").in(request.getState())));
            }
            if (!CollectionUtils.isEmpty(request.getType())) {
                predicates.add(cb.and(root.get("type").in(request.getType())));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        return applyJobs.map(applyJob -> new ApplyJobDTO(applyJob, false, false, false, true));
    }

    @Override
    @Transactional
    public ApplyJobDTO cancelApplyJob(Long applyJobId, String userAccount) {
        // 获取用户信息
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 获取申请详情并加锁
        ApplyJob applyJob = applyJobRepository.findByIdForUpdate(applyJobId)
                .orElseThrow(() -> new WrongParamException(ResultEnum.APPLYJOB_NOT_EXIST));
        // 验证申请人
        if(!applyJob.getApplyUser().getAccount().equals(userAccount)) {
            throw new BizException(ResultEnum.NOT_APPLY_USER);
        }
        // 验证申请为申请中状态
        if(!applyJob.getState().equals(ApplyJobStateEnum.APPLYING)) {
            throw new BizException(ResultEnum.APPLYJOB_STATE_CHANGE_NOT_ALLOWED);
        }
        // 更新申请信息
        applyJob.setState(ApplyJobStateEnum.CANCELLED);
        applyJob.setOperateUser(user);
        applyJob.setMsg("申请人撤销申请");
        applyJob.setOperateTime(new Date());
        // 落库
        applyJobRepository.save(applyJob);
        return new ApplyJobDTO(applyJob, false, false, false, false);
    }

    @Override
    @Transactional
    public OssFileDTO getApplyJobDownloadFileInfo(Long applyJobId, Boolean isUploadFile, String userAccount) {
        // 获取用户信息
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 获取申请详情
        ApplyJob applyJob = applyJobRepository.findById(applyJobId)
                .orElseThrow(() -> new WrongParamException(ResultEnum.APPLYJOB_NOT_EXIST));
        // 验证管理员 或 申请人
        if(!applyJob.getApplyUser().getId().equals(user.getId()) && !user.getType().equals(UserTypeEnum.ADMIN)) {
            throw new BizException(ResultEnum.NOT_APPLY_USER);
        }
        // 获取申请中的文件
        OssFile file = isUploadFile? applyJob.getUploadFile() : applyJob.getDownloadFile();
        Optional.ofNullable(file)
                .map(OssFile::getExpiresTime)
                .filter(date -> date.getTime() > System.currentTimeMillis())
                .orElseThrow(() -> new BizException(ResultEnum.FILE_NOT_EXIST_OR_EXPIRES));
        // 获取文件下载url
        String url = ossService.preSignedGetObject(file.getOssBucketName(), file.getOssObjectName());
        OssFileDTO ossFileDTO = new OssFileDTO(file);
        ossFileDTO.setUrl(url);
        return ossFileDTO;
    }
}
