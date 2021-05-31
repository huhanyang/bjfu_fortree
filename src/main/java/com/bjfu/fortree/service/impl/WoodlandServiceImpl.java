package com.bjfu.fortree.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperationDispatch;
import com.bjfu.fortree.config.MinioConfig;
import com.bjfu.fortree.enums.entity.FileTypeEnum;
import com.bjfu.fortree.pojo.dto.job.ApplyJobDTO;
import com.bjfu.fortree.pojo.dto.woodland.TreeDTO;
import com.bjfu.fortree.pojo.dto.woodland.WoodlandDTO;
import com.bjfu.fortree.pojo.dto.woodland.WoodlandDetailDTO;
import com.bjfu.fortree.pojo.entity.apply.ApplyJob;
import com.bjfu.fortree.pojo.entity.file.OssFile;
import com.bjfu.fortree.pojo.entity.user.User;
import com.bjfu.fortree.pojo.entity.woodland.Record;
import com.bjfu.fortree.pojo.entity.woodland.Tree;
import com.bjfu.fortree.pojo.entity.woodland.Woodland;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.exception.WrongParamException;
import com.bjfu.fortree.repository.file.OssFileRepository;
import com.bjfu.fortree.repository.job.ApplyJobRepository;
import com.bjfu.fortree.repository.user.AuthorityRepository;
import com.bjfu.fortree.repository.user.UserRepository;
import com.bjfu.fortree.repository.woodland.RecordRepository;
import com.bjfu.fortree.repository.woodland.TreeRepository;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import com.bjfu.fortree.pojo.request.woodland.*;
import com.bjfu.fortree.service.OssService;
import com.bjfu.fortree.service.WoodlandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    private ApprovedOperationDispatch approvedOperationDispatch;
    @Autowired
    private OssService ossService;
    @Autowired
    private OssFileRepository ossFileRepository;

    @Override
    @Transactional
    public ApplyJobDTO createWoodland(String userAccount, CreateWoodlandRequest request) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 请求参数序列化
        String applyParam = JSONObject.toJSONString(request);
        // 判断是否存在免审批权限
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.CREATE_ANY_WOODLAND)) {
            // 生成状态为通过的申请实体
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.CREATE_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.CREATE_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    @Transactional
    public ApplyJobDTO addRecord(String userAccount, AddRecordRequest request) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 查找林地并对其加锁
        Woodland woodland = woodlandRepository.findByIdForUpdate(request.getWoodlandId()).
                orElseThrow(() -> new WrongParamException(ResultEnum.WOODLAND_NOT_EXIST));
        // 请求参数序列化
        String applyParam = JSONObject.toJSONString(request);
        // 判断是否存在免审批权限 或 是否为林地创建人
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.ADD_RECORD_IN_ANY_WOODLAND) ||
                woodland.getCreator().getId().equals(user.getId())) {
            // 生成状态为通过的申请实体
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.ADD_RECORD_IN_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.ADD_RECORD_IN_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    @Transactional
    public ApplyJobDTO addTrees(String userAccount, AddTreesRequest request) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 查找记录并对其加锁
        Record record = recordRepository.findByIdForUpdate(request.getRecordId())
                .orElseThrow(() -> new WrongParamException(ResultEnum.RECORD_NOT_EXIST));
        // 请求参数序列化
        String applyParam = JSONObject.toJSONString(request);
        // 判断是否存在免审批权限 或 是否为林地创建人 或 是否为记录创建人
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.ADD_TREES_IN_ANY_RECORD) ||
                record.getWoodland().getCreator().getId().equals(user.getId()) ||
                record.getCreator().getId().equals(user.getId())) {
            // 生成状态为通过的申请实体
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.ADD_TREES_IN_RECORD, applyParam);
            // 申请实体落库
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.ADD_TREES_IN_RECORD, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    @Transactional
    public ApplyJobDTO addTreesByExcel(String userAccount, AddTreesByExcelRequest request) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 查找记录并对其加锁
        Record record = recordRepository.findByIdForUpdate(request.getRecordId())
                .orElseThrow(() -> new WrongParamException(ResultEnum.RECORD_NOT_EXIST));
        // 利用UUID生成存储到OSS的文件名
        String ossObjectName = UUID.randomUUID().toString();
        // 文件上传到OSS
        try {
            ossService.putObject(MinioConfig.APPLY_EXCEL_BUCKET_NAME, ossObjectName, request.getFile().getInputStream());
        } catch (IOException e) {
            throw new SystemWrongException(ResultEnum.FILE_UPLOAD_FAILED);
        }
        // 记录上传的文件信息
        OssFile ossFile = new OssFile();
        ossFile.setFileName(request.getFileName());
        ossFile.setType(FileTypeEnum.USER_APPLY_FILE);
        ossFile.setOssBucketName(MinioConfig.APPLY_EXCEL_BUCKET_NAME);
        ossFile.setOssObjectName(ossObjectName);
        ossFileRepository.save(ossFile);
        // 对请求参数中的recordId序列化
        String applyParam = request.getRecordId().toString();
        // 判断是否存在免审批权限 或 是否为林地创建人 或 是否为记录创建人
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.ADD_TREES_IN_ANY_RECORD) ||
                record.getWoodland().getCreator().getId().equals(user.getId()) ||
                record.getCreator().getId().equals(user.getId())) {
            // 生成状态为通过的申请实体
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.ADD_TREES_BY_EXCEL_IN_RECORD, applyParam);
            // 文件保存到申请实体的下载项中
            passedApply.setDownloadFile(ossFile);
            // 申请实体落库
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.ADD_TREES_BY_EXCEL_IN_RECORD, applyParam);
            // 文件保存到申请实体的下载项中
            apply.setDownloadFile(ossFile);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    @Transactional
    public ApplyJobDTO deleteWoodland(String userAccount, Long woodlandId) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 查找林地并对其加锁
        Woodland woodland = woodlandRepository.findByIdForUpdate(woodlandId).
                orElseThrow(() -> new WrongParamException(ResultEnum.WOODLAND_NOT_EXIST));
        // 请求参数序列化
        String applyParam = woodlandId.toString();
        // 判断是否存在免审批权限 或 是否为林地创建人
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.DELETE_ANY_WOODLAND) ||
                woodland.getCreator().getId().equals(user.getId())) {
            // 生成状态为通过的申请实体
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.DELETE_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.DELETE_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    @Transactional
    public ApplyJobDTO deleteRecord(String userAccount, Long recordId) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 查找记录并对其加锁
        Record record = recordRepository.findByIdForUpdate(recordId)
                .orElseThrow(() -> new WrongParamException(ResultEnum.RECORD_NOT_EXIST));
        // 请求参数序列化
        String applyParam = recordId.toString();
        // 判断是否存在免审批权限 或 是否为记录创建人 或 是否为林地创建人
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.DELETE_RECORD_IN_ANY_WOODLAND) ||
                record.getCreator().getId().equals(user.getId()) ||
                record.getWoodland().getCreator().getId().equals(user.getId())) {
            // 生成状态为通过的申请实体
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.DELETE_RECORD_IN_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.DELETE_RECORD_IN_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    @Transactional
    public ApplyJobDTO deleteTrees(String userAccount, DeleteTreesRequest request) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 查找记录并对其加锁
        Record record = recordRepository.findByIdForUpdate(request.getRecordId())
                .orElseThrow(() -> new WrongParamException(ResultEnum.RECORD_NOT_EXIST));
        // 过滤出记录中存在的树木id
        List<Long> treeIds = treeRepository.findAllById(request.getTreeIds())
                .stream()
                .filter(tree -> tree.getRecord().getId().equals(record.getId()))
                .map(Tree::getId)
                .collect(Collectors.toList());
        // 更新请求参数
        request.setTreeIds(treeIds);
        // 请求参数序列化
        String applyParam = JSONObject.toJSONString(request);
        // 判断是否存在免审批权限 或 是否为记录创建人 或 是否为林地创建人
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.DELETE_TREES_IN_ANY_RECORD) ||
                record.getCreator().getId().equals(user.getId()) ||
                record.getWoodland().getCreator().getId().equals(user.getId())) {
            // 生成状态为通过的申请实体
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.DELETE_TREES_IN_RECORD, applyParam);
            // 申请实体落库
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.DELETE_TREES_IN_RECORD, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    @Transactional
    public ApplyJobDTO editWoodland(String userAccount, EditWoodlandRequest request) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 查找林地并对其加锁
        Woodland woodland = woodlandRepository.findByIdForUpdate(request.getWoodlandId()).
                orElseThrow(() -> new WrongParamException(ResultEnum.WOODLAND_NOT_EXIST));
        // 请求参数序列化
        String applyParam = JSONObject.toJSONString(request);
        // 判断是否存在免审批权限 或 是否为林地创建人
        if(woodland.getCreator().getId().equals(user.getId()) ||
                authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.EDIT_ANY_WOODLAND)) {
            // 生成状态为通过的申请实体
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.EDIT_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.EDIT_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    @Transactional
    public ApplyJobDTO editRecord(String userAccount, EditRecordRequest request) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 查找记录并对其加锁
        Record record = recordRepository.findByIdForUpdate(request.getRecordId())
                .orElseThrow(() -> new WrongParamException(ResultEnum.RECORD_NOT_EXIST));
        // 请求参数序列化
        String applyParam = JSONObject.toJSONString(request);
        // 判断是否存在免审批权限 或 是否为记录创建人 或 是否为林地创建人
        if(record.getCreator().getId().equals(user.getId()) ||
                record.getWoodland().getCreator().getId().equals(user.getId()) ||
                authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.EDIT_RECORD_IN_ANY_WOODLAND)) {
            // 生成状态为通过的申请实体
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.EDIT_RECORD, applyParam);
            // 申请实体落库
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.EDIT_RECORD, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    public Page<WoodlandDTO> getWoodlands(GetWoodlandsRequest getWoodlandsRequest) {
        PageRequest pageRequest = PageRequest.of(getWoodlandsRequest.getCurrent() - 1, getWoodlandsRequest.getPageSize());
        if(getWoodlandsRequest.getField() != null) {
            Sort sort = Sort.by(new Sort.Order(getWoodlandsRequest.getOrder(), getWoodlandsRequest.getField()));
            pageRequest = PageRequest.of(getWoodlandsRequest.getCurrent() - 1, getWoodlandsRequest.getPageSize(), sort);
        }
        Page<Woodland> woodlands = woodlandRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!CollectionUtils.isEmpty(getWoodlandsRequest.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + getWoodlandsRequest.getName().get(0) + "%"));
            }
            if (!CollectionUtils.isEmpty(getWoodlandsRequest.getCountry())) {
                predicates.add(cb.like(root.get("country"), "%" + getWoodlandsRequest.getCountry().get(0) + "%"));
            }
            if (!CollectionUtils.isEmpty(getWoodlandsRequest.getProvince())) {
                predicates.add(cb.like(root.get("province"), "%" + getWoodlandsRequest.getProvince().get(0) + "%"));
            }
            if (!CollectionUtils.isEmpty(getWoodlandsRequest.getCity())) {
                predicates.add(cb.like(root.get("city"), "%" + getWoodlandsRequest.getCity().get(0) + "%"));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        return woodlands.map(WoodlandDTO::new);
    }

    @Override
    public List<WoodlandDTO> getAllWoodlands() {
        List<Woodland> woodlands = woodlandRepository.findAll();
        return woodlands.stream()
                .map(WoodlandDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<WoodlandDTO> getWoodlandsByCreator(String userAccount, GetWoodlandsByCreatorRequest getWoodlandsByCreatorRequest) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        PageRequest pageRequest = PageRequest.of(getWoodlandsByCreatorRequest.getCurrent() - 1, getWoodlandsByCreatorRequest.getPageSize());
        if(getWoodlandsByCreatorRequest.getField() != null) {
            Sort sort = Sort.by(new Sort.Order(getWoodlandsByCreatorRequest.getOrder(), getWoodlandsByCreatorRequest.getField()));
            pageRequest = PageRequest.of(getWoodlandsByCreatorRequest.getCurrent() - 1, getWoodlandsByCreatorRequest.getPageSize(), sort);
        }
        Page<Woodland> woodlands = woodlandRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("creator"), user));
            if (!CollectionUtils.isEmpty(getWoodlandsByCreatorRequest.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + getWoodlandsByCreatorRequest.getName().get(0) + "%"));
            }
            if (!CollectionUtils.isEmpty(getWoodlandsByCreatorRequest.getCountry())) {
                predicates.add(cb.like(root.get("country"), "%" + getWoodlandsByCreatorRequest.getCountry().get(0) + "%"));
            }
            if (!CollectionUtils.isEmpty(getWoodlandsByCreatorRequest.getProvince())) {
                predicates.add(cb.like(root.get("province"), "%" + getWoodlandsByCreatorRequest.getProvince().get(0) + "%"));
            }
            if (!CollectionUtils.isEmpty(getWoodlandsByCreatorRequest.getCity())) {
                predicates.add(cb.like(root.get("city"), "%" + getWoodlandsByCreatorRequest.getCity().get(0) + "%"));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        return woodlands.map(WoodlandDTO::new);
    }

    @Override
    public WoodlandDetailDTO getWoodlandDetail(Long woodlandId) {
        Woodland woodland = woodlandRepository.findById(woodlandId)
                .orElseThrow(() -> new WrongParamException(ResultEnum.WOODLAND_NOT_EXIST));
        return new WoodlandDetailDTO(woodland);
    }

    @Override
    public Page<TreeDTO> getTrees(GetTreesRequest getTreesRequest) {
        Optional<Record> recordOptional = recordRepository.findById(getTreesRequest.getRecordId());
        if(recordOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.RECORD_NOT_EXIST);
        }
        Record record = recordOptional.get();
        PageRequest pageRequest = PageRequest.of(getTreesRequest.getCurrent() - 1, getTreesRequest.getPageSize());
        if(getTreesRequest.getField() != null) {
            Sort sort = Sort.by(new Sort.Order(getTreesRequest.getOrder(), getTreesRequest.getField()));
            pageRequest = PageRequest.of(getTreesRequest.getCurrent() - 1, getTreesRequest.getPageSize(), sort);
        }
        Page<Tree> trees = treeRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!CollectionUtils.isEmpty(getTreesRequest.getTreeId())) {
                predicates.add(cb.like(root.get("treeId"), "%" + getTreesRequest.getTreeId().get(0) + "%"));
            }
            if (!CollectionUtils.isEmpty(getTreesRequest.getSpecies())) {
                predicates.add(cb.like(root.get("species"), "%" + getTreesRequest.getSpecies().get(0) + "%"));
            }
            predicates.add(cb.equal(root.get("record"), record));
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        return trees.map(TreeDTO::new);
    }

}