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
import com.bjfu.fortree.enums.entity.UserTypeEnum;
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
import com.bjfu.fortree.pojo.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    @Transactional(rollbackFor = RuntimeException.class)
    public ApplyJobDTO addTreesByExcel(String userAccount, AddTreesByExcelRequest addTreesByExcelRequest) {
        // 查找用户、记录、用户拥有的权限
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        Optional<Record> recordOptional = recordRepository.findByIdForUpdate(addTreesByExcelRequest.getRecordId());
        if(recordOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.RECORD_NOT_EXIST);
        }
        String ossObjectName = UUID.randomUUID().toString();
        try {
            ossService.putObject(MinioConfig.APPLY_EXCEL_BUCKET_NAME, ossObjectName, addTreesByExcelRequest.getFile().getInputStream());
        } catch (IOException e) {
            throw new WrongParamException(ResultEnum.FILE_UPLOAD_FAILED);
        }
        // 记录上传oss的文件信息
        OssFile ossFile = new OssFile();
        ossFile.setFileName(addTreesByExcelRequest.getFileName());
        ossFile.setType(FileTypeEnum.USER_APPLY_FILE);
        ossFile.setOssBucketName(MinioConfig.APPLY_EXCEL_BUCKET_NAME);
        ossFile.setOssObjectName(ossObjectName);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        ossFile.setExpiresTime(calendar.getTime());
        ossFileRepository.save(ossFile);

        Record record = recordOptional.get();
        String applyParam = addTreesByExcelRequest.getRecordId().toString();
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.ADD_TREES_IN_ANY_RECORD) ||
                record.getWoodland().getCreator().getId().equals(user.getId()) ||
                record.getCreator().getId().equals(user.getId())) {
            // 根据权限、林地创建人及记录创建人判断不需要审批则直接保存审批通过的申请
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.ADD_TREES_BY_EXCEL_IN_RECORD, applyParam);
            passedApply.setDownloadFile(ossFile);
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作器来落库
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.ADD_TREES_BY_EXCEL_IN_RECORD, applyParam);
            apply.setDownloadFile(ossFile);
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
    @Transactional(rollbackFor = RuntimeException.class)
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
    @Transactional(rollbackFor = RuntimeException.class)
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

    @Override
    public PageVO<WoodlandDTO> getWoodlands(GetWoodlandsRequest getWoodlandsRequest) {
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
        List<WoodlandDTO> woodlandDTOList = woodlands.getContent().stream().map(WoodlandDTO::new).collect(Collectors.toList());
        return new PageVO<>(woodlands.getTotalElements(), woodlandDTOList);
    }

    @Override
    public List<WoodlandDTO> getAllWoodlands() {
        List<Woodland> woodlands = woodlandRepository.findAll();
        return woodlands.stream().map(WoodlandDTO::new).collect(Collectors.toList());
    }

    @Override
    public PageVO<WoodlandDTO> getWoodlandsByCreator(String userAccount, GetWoodlandsByCreatorRequest getWoodlandsByCreatorRequest) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        PageRequest pageRequest = PageRequest.of(getWoodlandsByCreatorRequest.getCurrent() - 1, getWoodlandsByCreatorRequest.getPageSize());
        if(getWoodlandsByCreatorRequest.getField() != null) {
            Sort sort = Sort.by(new Sort.Order(getWoodlandsByCreatorRequest.getOrder(), getWoodlandsByCreatorRequest.getField()));
            pageRequest = PageRequest.of(getWoodlandsByCreatorRequest.getCurrent() - 1, getWoodlandsByCreatorRequest.getPageSize(), sort);
        }
        Page<Woodland> woodlands = woodlandRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
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
            if(!user.getType().equals(UserTypeEnum.ADMIN)) {
                predicates.add(cb.equal(root.get("creator"), user));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        List<WoodlandDTO> woodlandDTOList = woodlands.getContent().stream().map(WoodlandDTO::new).collect(Collectors.toList());
        return new PageVO<>(woodlands.getTotalElements(), woodlandDTOList);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public WoodlandDetailDTO getWoodlandDetail(Long woodlandId) {
        Optional<Woodland> woodlandOptional = woodlandRepository.findById(woodlandId);
        if(woodlandOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.WOODLAND_NOT_EXIST);
        }
        Woodland woodland = woodlandOptional.get();
        return new WoodlandDetailDTO(woodland);
    }

    @Override
    public PageVO<TreeDTO> getTrees(GetTreesRequest getTreesRequest) {
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
        List<TreeDTO> treeDtoS = trees.getContent().stream().map(TreeDTO::new).collect(Collectors.toList());
        return new PageVO<>(trees.getTotalElements(), treeDtoS);
    }

}