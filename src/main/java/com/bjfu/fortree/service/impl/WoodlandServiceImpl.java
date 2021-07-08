package com.bjfu.fortree.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperationDispatch;
import com.bjfu.fortree.config.MinioConfig;
import com.bjfu.fortree.pojo.dto.ApplyJobDTO;
import com.bjfu.fortree.pojo.dto.TreeDTO;
import com.bjfu.fortree.pojo.dto.WoodlandDTO;
import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.pojo.entity.OssFile;
import com.bjfu.fortree.pojo.entity.User;
import com.bjfu.fortree.pojo.entity.Record;
import com.bjfu.fortree.pojo.entity.Tree;
import com.bjfu.fortree.pojo.entity.Woodland;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.exception.WrongParamException;
import com.bjfu.fortree.pojo.request.BasePageAndSorterRequest;
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
import org.springframework.util.StringUtils;

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
            return new ApplyJobDTO(passedApply, false, false, false, false);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.CREATE_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply, false, false, false, false);
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
            return new ApplyJobDTO(passedApply, false, false, false ,false);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.ADD_RECORD_IN_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply, false, false, false, false);
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
            return new ApplyJobDTO(passedApply, false, false, false, false);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.ADD_TREES_IN_RECORD, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply, false, false, false, false);
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
            // 文件保存到申请实体的上传项中
            passedApply.setUploadFile(ossFile);
            // 申请实体落库
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作
            approvedOperationDispatch.dispatch(passedApply);
            return new ApplyJobDTO(passedApply, false, false, false, false);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.ADD_TREES_BY_EXCEL_IN_RECORD, applyParam);
            // 文件保存到申请实体的下载项中
            apply.setDownloadFile(ossFile);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply, false, false, false, false);
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
            return new ApplyJobDTO(passedApply, false, false, false, false);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.DELETE_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply, false, false, false, false);
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
            return new ApplyJobDTO(passedApply, false, false, false, false);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.DELETE_RECORD_IN_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply, false, false, false, false);
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
            return new ApplyJobDTO(passedApply, false, false, false, false);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.DELETE_TREES_IN_RECORD, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply, false, false, false, false);
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
            return new ApplyJobDTO(passedApply, false, false, false, false);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.EDIT_WOODLAND, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply, false, false, false, false);
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
            return new ApplyJobDTO(passedApply, false, false, false, false);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.EDIT_RECORD, applyParam);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply, false, false, false, false);
        }
    }

    /**
     * 林地实体属性顺序
     */
    private static final Map<String, Integer> WOODLAND_FIELD_ORDER_WEIGHT = new HashMap<>();
    static {
        WOODLAND_FIELD_ORDER_WEIGHT.put("name", 1);
        WOODLAND_FIELD_ORDER_WEIGHT.put("country", 2);
        WOODLAND_FIELD_ORDER_WEIGHT.put("province", 3);
        WOODLAND_FIELD_ORDER_WEIGHT.put("city", 4);
        WOODLAND_FIELD_ORDER_WEIGHT.put("shape", 5);
        WOODLAND_FIELD_ORDER_WEIGHT.put("length", 6);
        WOODLAND_FIELD_ORDER_WEIGHT.put("width", 7);
        WOODLAND_FIELD_ORDER_WEIGHT.put("createdTime", 8);

    }

    @Override
    public Page<WoodlandDTO> getWoodlands(GetWoodlandsRequest request) {
        BasePageAndSorterRequest.Pagination pagination = request.getPagination();
        // 构建多属性排序
        List<Sort.Order> orders = Optional.ofNullable(request.getSorter()).orElse(new LinkedList<>())
                .stream()
                .filter(singleSorter -> StringUtils.hasText(singleSorter.getField()))
                .sorted(Comparator.comparingInt(s -> WOODLAND_FIELD_ORDER_WEIGHT.get(s.getField())))
                .map(singleSorter -> new Sort.Order(singleSorter.getOrder(), singleSorter.getField()))
                .collect(Collectors.toList());
        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(pagination.getCurrent() - 1, pagination.getPageSize(), Sort.by(orders));
        // 执行查询
        Page<Woodland> woodlands = woodlandRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(request.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + request.getName() + "%"));
            }
            if (StringUtils.hasText(request.getCountry())) {
                predicates.add(cb.like(root.get("country"), "%" + request.getCountry() + "%"));
            }
            if (StringUtils.hasText(request.getProvince())) {
                predicates.add(cb.like(root.get("province"), "%" + request.getProvince() + "%"));
            }
            if (StringUtils.hasText(request.getCity())) {
                predicates.add(cb.like(root.get("city"), "%" + request.getCity() + "%"));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        return woodlands.map(woodland -> new WoodlandDTO(woodland, true, false));
    }

    @Override
    public List<WoodlandDTO> getAllWoodlands() {
        List<Woodland> woodlands = woodlandRepository.findAll();
        return woodlands.stream()
                .map(woodland -> new WoodlandDTO(woodland, false, false))
                .collect(Collectors.toList());
    }

    @Override
    public List<WoodlandDTO> getAllWoodlands(GetAllWoodlandsRequest request) {
        List<Woodland> woodlands = woodlandRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(request.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + request.getName() + "%"));
            }
            if (StringUtils.hasText(request.getCountry())) {
                predicates.add(cb.like(root.get("country"), "%" + request.getCountry() + "%"));
            }
            if (StringUtils.hasText(request.getProvince())) {
                predicates.add(cb.like(root.get("province"), "%" + request.getProvince() + "%"));
            }
            if (StringUtils.hasText(request.getCity())) {
                predicates.add(cb.like(root.get("city"), "%" + request.getCity() + "%"));
            }
            if (request.getAreaDirection() != null && request.getArea() != null) {
                if(request.getAreaDirection().equals(GetAllWoodlandsRequest.NumberDirection.MIN)) {
                    predicates.add(cb.greaterThanOrEqualTo(cb.prod(root.get("length"), root.get("width")), request.getArea()));
                } else {
                    predicates.add(cb.lessThanOrEqualTo(cb.prod(root.get("length"), root.get("width")), request.getArea()));
                }
            }
            if (request.getTreeCountDirection() != null && request.getTreeCount() != null) {
                if(request.getTreeCountDirection().equals(GetAllWoodlandsRequest.NumberDirection.MIN)) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("newRecord").get("treeCount"), request.getTreeCount()));
                } else {
                    predicates.add(cb.lessThanOrEqualTo(root.get("newRecord").get("treeCount"), request.getTreeCount()));
                }
            }
            if (request.getTreeMeanHeightDirection() != null && request.getTreeMeanHeight() != null) {
                if(request.getTreeMeanHeightDirection().equals(GetAllWoodlandsRequest.NumberDirection.MIN)) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("newRecord").get("meanHeight"), request.getTreeMeanHeight()));
                } else {
                    predicates.add(cb.lessThanOrEqualTo(root.get("newRecord").get("meanHeight"), request.getTreeMeanHeight()));
                }
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        });
        return woodlands.stream()
                .map(woodland -> new WoodlandDTO(woodland, false, false))
                .collect(Collectors.toList());
    }

    @Override
    public Page<WoodlandDTO> getWoodlandsByCreator(String userAccount, GetWoodlandsRequest request) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        BasePageAndSorterRequest.Pagination pagination = request.getPagination();
        // 构建多属性排序
        List<Sort.Order> orders = Optional.ofNullable(request.getSorter()).orElse(new LinkedList<>())
                .stream()
                .filter(singleSorter -> StringUtils.hasText(singleSorter.getField()))
                .sorted(Comparator.comparingInt(s -> WOODLAND_FIELD_ORDER_WEIGHT.get(s.getField())))
                .map(singleSorter -> new Sort.Order(singleSorter.getOrder(), singleSorter.getField()))
                .collect(Collectors.toList());
        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(pagination.getCurrent() - 1, pagination.getPageSize(), Sort.by(orders));
        // 执行查询
        Page<Woodland> woodlands = woodlandRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("creator"), user));
            if (StringUtils.hasText(request.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + request.getName() + "%"));
            }
            if (StringUtils.hasText(request.getCountry())) {
                predicates.add(cb.like(root.get("country"), "%" + request.getCountry() + "%"));
            }
            if (StringUtils.hasText(request.getProvince())) {
                predicates.add(cb.like(root.get("province"), "%" + request.getProvince() + "%"));
            }
            if (StringUtils.hasText(request.getCity())) {
                predicates.add(cb.like(root.get("city"), "%" + request.getCity() + "%"));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        return woodlands.map(woodland -> new WoodlandDTO(woodland, false, false));
    }

    @Override
    @Transactional
    public WoodlandDTO getWoodlandDetail(Long woodlandId) {
        Woodland woodland = woodlandRepository.findById(woodlandId)
                .orElseThrow(() -> new WrongParamException(ResultEnum.WOODLAND_NOT_EXIST));
        return new WoodlandDTO(woodland, true, true);
    }

    /**
     * 树木实体属性顺序
     */
    private static final Map<String, Integer> TREE_FIELD_ORDER_WEIGHT = new HashMap<>();
    static {
        TREE_FIELD_ORDER_WEIGHT.put("treeId", 1);
        TREE_FIELD_ORDER_WEIGHT.put("species", 2);
        TREE_FIELD_ORDER_WEIGHT.put("height", 3);
        TREE_FIELD_ORDER_WEIGHT.put("dbh", 4);
        TREE_FIELD_ORDER_WEIGHT.put("crownWidth", 5);
    }

    @Override
    public Page<TreeDTO> getTrees(GetTreesRequest request) {
        // 获取记录信息
        Record record = recordRepository.findById(request.getRecordId())
                .orElseThrow(() -> new WrongParamException(ResultEnum.RECORD_NOT_EXIST));
        BasePageAndSorterRequest.Pagination pagination = request.getPagination();
        // 构建多属性排序
        List<Sort.Order> orders = Optional.ofNullable(request.getSorter()).orElse(new LinkedList<>())
                .stream()
                .filter(singleSorter -> StringUtils.hasText(singleSorter.getField()))
                .sorted(Comparator.comparingInt(s -> TREE_FIELD_ORDER_WEIGHT.get(s.getField())))
                .map(singleSorter -> new Sort.Order(singleSorter.getOrder(), singleSorter.getField()))
                .collect(Collectors.toList());
        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(pagination.getCurrent() - 1, pagination.getPageSize(), Sort.by(orders));
        // 执行查询
        Page<Tree> trees = treeRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("record"), record));
            if (StringUtils.hasText(request.getTreeId())) {
                predicates.add(cb.like(root.get("treeId"), "%" + request.getTreeId() + "%"));
            }
            if (StringUtils.hasText(request.getSpecies())) {
                predicates.add(cb.like(root.get("species"), "%" + request.getSpecies() + "%"));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        return trees.map(tree -> new TreeDTO(tree, false));
    }

}