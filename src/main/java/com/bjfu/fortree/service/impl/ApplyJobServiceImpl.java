package com.bjfu.fortree.service.impl;

import com.bjfu.fortree.approval.ApprovedOperationDispatch;
import com.bjfu.fortree.exception.*;
import com.bjfu.fortree.pojo.dto.file.FileDownloadDTO;
import com.bjfu.fortree.pojo.dto.job.ApplyJobDTO;
import com.bjfu.fortree.pojo.entity.apply.ApplyJob;
import com.bjfu.fortree.pojo.entity.file.OssFile;
import com.bjfu.fortree.pojo.entity.user.User;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.repository.file.OssFileRepository;
import com.bjfu.fortree.repository.job.ApplyJobRepository;
import com.bjfu.fortree.repository.user.UserRepository;
import com.bjfu.fortree.pojo.request.apply.ApprovalApplyJobRequest;
import com.bjfu.fortree.pojo.request.apply.GetAllApplyJobRequest;
import com.bjfu.fortree.pojo.request.apply.GetMyApplyJobRequest;
import com.bjfu.fortree.service.ApplyJobService;
import com.bjfu.fortree.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import java.util.*;

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
    private OssFileRepository ossFileRepository;
    @Autowired
    private OssService ossService;
    @Autowired
    private ApprovedOperationDispatch approvedOperationDispatch;

    @Override
    public Page<ApplyJobDTO> getAllApplyJob(GetAllApplyJobRequest getAllApplyJobRequest) {
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
        return applyJobs.map(ApplyJobDTO::new);
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
        return new ApplyJobDTO(applyJob);
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
        return new ApplyJobDTO(applyJob);
    }

    @Override
    public Page<ApplyJobDTO> getApplyJobByApplyUser(GetMyApplyJobRequest getMyApplyJobRequest, String userAccount) {
        // 获取用户信息
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 构建分页与排序请求
        PageRequest pageRequest = PageRequest.of(getMyApplyJobRequest.getCurrent() - 1, getMyApplyJobRequest.getPageSize());
        if(getMyApplyJobRequest.getField() != null) {
            Sort sort = Sort.by(new Sort.Order(getMyApplyJobRequest.getOrder(), getMyApplyJobRequest.getField()));
            pageRequest = PageRequest.of(getMyApplyJobRequest.getCurrent() - 1, getMyApplyJobRequest.getPageSize(), sort);
        }
        // 查询
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
        return applyJobs.map(ApplyJobDTO::new);
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
        return new ApplyJobDTO(applyJob);
    }

    @Override
    @Transactional
    public FileDownloadDTO getApplyJobDownloadFileUrl(Long applyJobId, String userAccount) {
        // 获取用户信息
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 获取申请详情并加锁
        ApplyJob applyJob = applyJobRepository.findByIdForUpdate(applyJobId)
                .orElseThrow(() -> new WrongParamException(ResultEnum.APPLYJOB_NOT_EXIST));
        // 验证管理员 或 申请人
        if(!applyJob.getApplyUser().getId().equals(user.getId()) && !user.getType().equals(UserTypeEnum.ADMIN)) {
            throw new BizException(ResultEnum.NOT_APPLY_USER);
        }
        // 获取申请中的文件
        OssFile downloadFile = applyJob.getDownloadFile();
        Optional.ofNullable(downloadFile)
                .map(OssFile::getExpiresTime)
                .filter(date -> date.getTime() > System.currentTimeMillis())
                .orElseThrow(() -> new BizException(ResultEnum.FILE_NOT_EXIST_OR_EXPIRES));
        // 获取文件下载url
        String downloadUrl = downloadFile.getDownloadUrl();
        if(downloadUrl == null || downloadFile.getDownloadUrlExpiresTime().getTime() < System.currentTimeMillis()) {
            // 下载链接失效 更新下载链接
            downloadUrl = ossService.preSignedGetObject(downloadFile.getOssBucketName(), downloadFile.getOssObjectName());
            Date downloadUrlExpiresTime = new Date(System.currentTimeMillis() + MinioOssServiceImpl.DEFAULT_GET_OBJECT_EXPIRES);
            downloadFile.setDownloadUrl(downloadUrl);
            if(downloadFile.getExpiresTime().getTime() < downloadUrlExpiresTime.getTime()) {
                downloadUrlExpiresTime = downloadFile.getExpiresTime();
            }
            downloadFile.setDownloadUrlExpiresTime(downloadUrlExpiresTime);
            ossFileRepository.save(downloadFile);
        }
        return new FileDownloadDTO(downloadFile);
    }
}
