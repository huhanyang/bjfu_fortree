package com.bjfu.fortree.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperationDispatch;
import com.bjfu.fortree.dto.job.ApplyJobDTO;
import com.bjfu.fortree.entity.apply.ApplyJob;
import com.bjfu.fortree.entity.user.User;
import com.bjfu.fortree.entity.woodland.Record;
import com.bjfu.fortree.entity.woodland.Tree;
import com.bjfu.fortree.entity.woodland.Woodland;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.exception.WrongParamException;
import com.bjfu.fortree.repository.job.ApplyJobRepository;
import com.bjfu.fortree.repository.user.AuthorityRepository;
import com.bjfu.fortree.repository.user.UserRepository;
import com.bjfu.fortree.repository.woodland.RecordRepository;
import com.bjfu.fortree.repository.woodland.TreeRepository;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import com.bjfu.fortree.request.woodland.*;
import com.bjfu.fortree.service.WoodlandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class WoodlandServiceImpl implements WoodlandService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private WoodlandRepository woodlandRepository;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private TreeRepository treeRepository;
    @Autowired
    private ApplyJobRepository applyJobRepository;
    @Autowired
    ApprovedOperationDispatch approvedOperationDispatch;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ApplyJobDTO createWoodland(String userAccount, CreateWoodlandRequest createWoodlandRequest) {
        // 查找用户及其拥有的权限
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        // 请求序列化成json便于保存到申请表中
        String applyParam = JSONObject.toJSONString(createWoodlandRequest);
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.CREATE_ANY_WOODLAND)) {
            // 根据权限判断不需要审批则直接保存审批通过的申请
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.CREATE_WOODLAND, applyParam);
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作器来落库
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            // 将需要审批的请求申请落库
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.CREATE_WOODLAND, applyParam);
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ApplyJobDTO addRecord(String userAccount, AddRecordRequest addRecordRequest) {
        // 查找用户、林地、用户拥有的权限
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        Optional<Woodland> woodlandOptional = woodlandRepository.findByIdForUpdate(addRecordRequest.getWoodlandId());
        if(woodlandOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.WOODLAND_NOT_EXIST);
        }
        Woodland woodland = woodlandOptional.get();
        // 请求序列化成json便于保存到申请表中
        String applyParam = JSONObject.toJSONString(addRecordRequest);
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.ADD_RECORD_IN_ANY_WOODLAND) ||
                woodland.getCreator().getId().equals(user.getId())) {
            // 根据权限及林地创建人判断不需要审批则直接保存审批通过的申请
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.ADD_RECORD_IN_WOODLAND, applyParam);
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作器来落库
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.ADD_RECORD_IN_WOODLAND, applyParam);
            // 将需要审批的请求申请落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ApplyJobDTO addTrees(String userAccount, AddTreesRequest addTreesRequest) {
        // 查找用户、记录、用户拥有的权限
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        Optional<Record> recordOptional = recordRepository.findByIdForUpdate(addTreesRequest.getRecordId());
        if(recordOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.RECORD_NOT_EXIST);
        }
        Record record = recordOptional.get();
        // 请求序列化成json便于保存到申请表中
        String applyParam = JSONObject.toJSONString(addTreesRequest);
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.ADD_TREES_IN_ANY_RECORD) ||
                record.getWoodland().getCreator().getId().equals(user.getId()) ||
                record.getCreator().getId().equals(user.getId())) {
            // 根据权限、林地创建人及记录创建人判断不需要审批则直接保存审批通过的申请
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.ADD_TREES_IN_RECORD, applyParam);
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作器来落库
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.ADD_TREES_IN_RECORD, applyParam);
            // 将需要审批的请求申请落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    public ApplyJobDTO deleteWoodland(String userAccount, Long woodlandId) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        Optional<Woodland> woodlandOptional = woodlandRepository.findById(woodlandId);
        if(woodlandOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.WOODLAND_NOT_EXIST);
        }
        Woodland woodland = woodlandOptional.get();
        // 请求序列化便于保存到申请表中
        String applyParam = woodlandId.toString();
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.DELETE_ANY_WOODLAND) ||
                woodland.getCreator().getId().equals(user.getId())) {
            // 根据权限、林地创建人及记录创建人判断不需要审批则直接保存审批通过的申请
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.DELETE_WOODLAND, applyParam);
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作器来落库
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.DELETE_WOODLAND, applyParam);
            // 将需要审批的请求申请落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    public ApplyJobDTO deleteRecord(String userAccount, Long recordId) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        Optional<Record> recordOptional = recordRepository.findByIdForUpdate(recordId);
        if(recordOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.RECORD_NOT_EXIST);
        }
        Record record = recordOptional.get();
        // 请求序列化便于保存到申请表中
        String applyParam = recordId.toString();
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.DELETE_RECORD_IN_ANY_WOODLAND) ||
                record.getCreator().getId().equals(user.getId()) ||
                record.getWoodland().getCreator().getId().equals(user.getId())) {
            // 根据权限、林地创建人及记录创建人判断不需要审批则直接保存审批通过的申请
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.DELETE_RECORD_IN_WOODLAND, applyParam);
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作器来落库
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.DELETE_RECORD_IN_WOODLAND, applyParam);
            // 将需要审批的请求申请落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    public ApplyJobDTO deleteTrees(String userAccount, DeleteTreesRequest deleteTreesRequest) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        Optional<Record> recordOptional = recordRepository.findByIdForUpdate(deleteTreesRequest.getRecordId());
        if(recordOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.RECORD_NOT_EXIST);
        }
        Record record = recordOptional.get();
        Iterable<Tree> trees = treeRepository.findAllById(deleteTreesRequest.getTreeIds());
        trees.forEach(tree -> {
            if(!tree.getRecord().getId().equals(record.getId())) {
                throw new WrongParamException(ResultEnum.TREE_IS_NOT_IN_RECORD);
            }
        });
        // 请求序列化成json便于保存到申请表中
        String applyParam = JSONObject.toJSONString(deleteTreesRequest);
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.DELETE_TREES_IN_ANY_RECORD) ||
                record.getCreator().getId().equals(user.getId()) ||
                record.getWoodland().getCreator().getId().equals(user.getId())) {
            // 根据权限、林地创建人及记录创建人判断不需要审批则直接保存审批通过的申请
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.DELETE_TREES_IN_RECORD, applyParam);
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作器来落库
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.DELETE_TREES_IN_RECORD, applyParam);
            // 将需要审批的请求申请落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    public ApplyJobDTO editWoodland(String userAccount, EditWoodlandRequest editWoodlandRequest) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        Optional<Woodland> woodlandOptional = woodlandRepository.findById(editWoodlandRequest.getWoodlandId());
        if(woodlandOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.WOODLAND_NOT_EXIST);
        }
        Woodland woodland = woodlandOptional.get();
        // 请求序列化成json便于保存到申请表中
        String applyParam = JSONObject.toJSONString(editWoodlandRequest);
        if(woodland.getCreator().getId().equals(user.getId()) ||
                authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.EDIT_ANY_WOODLAND)) {
            // 根据权限、林地创建人及记录创建人判断不需要审批则直接保存审批通过的申请
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.EDIT_WOODLAND, applyParam);
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作器来落库
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.EDIT_WOODLAND, applyParam);
            // 将需要审批的请求申请落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    public ApplyJobDTO editRecord(String userAccount, EditRecordRequest editRecordRequest) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        Optional<Record> recordOptional = recordRepository.findById(editRecordRequest.getRecordId());
        if(recordOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.WOODLAND_NOT_EXIST);
        }
        Record record = recordOptional.get();
        // 请求序列化成json便于保存到申请表中
        String applyParam = JSONObject.toJSONString(editRecordRequest);
        if(record.getCreator().getId().equals(user.getId()) ||
                record.getWoodland().getCreator().getId().equals(user.getId()) ||
                authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.EDIT_RECORD_IN_ANY_WOODLAND)) {
            // 根据权限、林地创建人及记录创建人判断不需要审批则直接保存审批通过的申请
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.EDIT_RECORD, applyParam);
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作器来落库
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.EDIT_RECORD, applyParam);
            // 将需要审批的请求申请落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

}