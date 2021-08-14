package com.bjfu.fortree.service.impl;

import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.exception.BizException;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.exception.WrongParamException;
import com.bjfu.fortree.pojo.dto.UserDTO;
import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.pojo.entity.Authority;
import com.bjfu.fortree.pojo.entity.User;
import com.bjfu.fortree.pojo.request.BasePageAndSorterRequest;
import com.bjfu.fortree.pojo.request.user.*;
import com.bjfu.fortree.repository.job.ApplyJobRepository;
import com.bjfu.fortree.repository.user.AuthorityRepository;
import com.bjfu.fortree.repository.user.UserRepository;
import com.bjfu.fortree.service.UserService;
import com.bjfu.fortree.util.EncryptionUtil;
import com.bjfu.fortree.util.UserInfoContextUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author warthog
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * 用户实体属性顺序
     */
    private static final Map<String, Integer> USER_FIELD_ORDER_WEIGHT = new HashMap<>();

    static {
        USER_FIELD_ORDER_WEIGHT.put("account", 1);
        USER_FIELD_ORDER_WEIGHT.put("type", 2);
        USER_FIELD_ORDER_WEIGHT.put("state", 3);
        USER_FIELD_ORDER_WEIGHT.put("name", 4);
        USER_FIELD_ORDER_WEIGHT.put("organization", 5);
        USER_FIELD_ORDER_WEIGHT.put("createdTime", 6);


    }

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private ApplyJobRepository applyJobRepository;

    @Override
    public UserDTO login(LoginCheckRequest request) {
        // 根据账号查询用户信息
        User user = userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new BizException(ResultEnum.ACCOUNT_NOT_EXIST_OR_PASSWORD_WRONG));
        // 判断密码是否匹配
        if (!user.getPassword().equals(EncryptionUtil.md5Encode(request.getPassword()))) {
            throw new BizException(ResultEnum.PASSWORD_WRONG);
        }
        // 判断用户是否被封禁
        if (user.getState().equals(UserStateEnum.BANNED)) {
            throw new BizException(ResultEnum.ACCOUNT_BANNED);
        }
        // 判断用户是否激活
        if (user.getState().equals(UserStateEnum.UNACTIVE)) {
            throw new BizException(ResultEnum.ACCOUNT_UNACTIVE);
        }
        // 返回用户信息
        return new UserDTO(user, false, false, false, false);
    }

    @Override
    @Transactional
    public UserDTO register(RegisterRequest request) {
        // 判断账号是否存在
        userRepository.findByAccountForUpdate(request.getAccount()).ifPresent((user) -> {
            throw new BizException(ResultEnum.ACCOUNT_EXIST);
        });
        // 创建新用户
        User user = new User();
        user.setType(UserTypeEnum.USER);
        user.setState(UserStateEnum.UNACTIVE);
        BeanUtils.copyProperties(request, user);
        user.setPassword(EncryptionUtil.md5Encode(request.getPassword()));
        // 落库
        userRepository.save(user);
        // 提交注册申请
        // 生成状态为申请中的申请实体
        ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.CONFIRM_REGISTER, request.getAccount());
        // 申请实体落库
        applyJobRepository.save(apply);
        // 返回用户信息
        return new UserDTO(user, false, false, false, false);
    }

    @Override
    @Transactional
    public UserDTO getInfo(String userAccount) {
        // 只有管理员可以获取其他用户的信息
        UserDTO userInfo = UserInfoContextUtil.getUserInfo()
                .orElseThrow(() -> new SystemWrongException(ResultEnum.USER_INFO_CONTEXT_WRONG));
        if (!userInfo.getAccount().equals(userAccount) && !userInfo.getType().equals(UserTypeEnum.ADMIN)) {
            throw new BizException(ResultEnum.REQUIRE_ADMIN);
        }
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        return new UserDTO(user, true, true, true, false);
    }

    @Override
    public UserDTO getInfoForContext(String userAccount) {
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        return new UserDTO(user, false, false, false, false);
    }

    @Override
    @Transactional
    public void changePassword(String userAccount, ChangePasswordRequest request) {
        // 根据账号加锁查询用户信息
        User user = userRepository.findByAccountForUpdate(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 密码验证
        if (!user.getPassword().equals(EncryptionUtil.md5Encode(request.getOldPassword()))) {
            throw new BizException(ResultEnum.PASSWORD_WRONG);
        }
        // 修改密码
        user.setPassword(EncryptionUtil.md5Encode(request.getNewPassword()));
        // 落库
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void grantUserAuthority(GrantUserAuthorityRequest request) {
        // 根据账号加锁查询用户信息
        User user = userRepository.findByAccountForUpdate(request.getAccount())
                .orElseThrow(() -> new WrongParamException(ResultEnum.USER_NOT_EXIST));
        // 获取用户当前拥有的权限
        List<Authority> userAuthorities = user.getAuthorities();
        // 创建用户当前存在的权限列表
        Set<AuthorityTypeEnum> existAuthorities = userAuthorities.stream()
                .map(Authority::getType)
                .collect(Collectors.toSet());
        // 过滤出当前不存在的权限
        List<Authority> authorities = request.getAuthorities()
                .stream()
                .filter(type -> !existAuthorities.contains(type))
                .map(type -> {
                    Authority authority = new Authority();
                    authority.setUser(user);
                    authority.setType(type);
                    return authority;
                })
                .collect(Collectors.toList());
        // 保存不存在的权限
        authorityRepository.saveAll(authorities);
    }

    @Override
    @Transactional
    public void revokeUserAuthority(RevokeUserAuthorityRequest request) {
        // 根据账号加锁查询用户信息
        User user = userRepository.findByAccountForUpdate(request.getAccount())
                .orElseThrow(() -> new WrongParamException(ResultEnum.USER_NOT_EXIST));
        // 获取用户当前拥有的权限
        List<Authority> userAuthorities = user.getAuthorities();
        // 创建权限与权限实体对应关系
        Map<AuthorityTypeEnum, Authority> existAuthorities = userAuthorities.stream()
                .collect(Collectors.toMap(Authority::getType, authority -> authority));
        // 过滤出存在的权限
        List<Authority> needDeleteAuthorities = request.getAuthorities()
                .stream()
                .map(existAuthorities::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // 删除存在的权限列表
        user.getAuthorities().removeAll(needDeleteAuthorities);
        authorityRepository.deleteAll(needDeleteAuthorities);
    }

    @Override
    public Page<UserDTO> getUsers(GetUsersRequest request) {
        BasePageAndSorterRequest.Pagination pagination = request.getPagination();
        // 构建多属性排序
        List<Sort.Order> orders = Optional.ofNullable(request.getSorter()).orElse(new LinkedList<>())
                .stream()
                .filter(singleSorter -> StringUtils.hasText(singleSorter.getField()))
                .sorted(Comparator.comparingInt(s -> USER_FIELD_ORDER_WEIGHT.get(s.getField())))
                .map(singleSorter -> new Sort.Order(singleSorter.getOrder(), singleSorter.getField()))
                .collect(Collectors.toList());
        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(pagination.getCurrent() - 1, pagination.getPageSize(), Sort.by(orders));
        // 执行查询
        Page<User> users = userRepository.findAll((root, query, cb) -> {
            // 生成其他匹配查询
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(request.getAccount())) {
                predicates.add(cb.like(root.get("account"), "%" + request.getAccount() + "%"));
            }
            if (StringUtils.hasText(request.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + request.getName() + "%"));
            }
            if (StringUtils.hasText(request.getOrganization())) {
                predicates.add(cb.like(root.get("organization"), "%" + request.getOrganization() + "%"));
            }
            if (!CollectionUtils.isEmpty(request.getState())) {
                predicates.add(cb.and(root.get("state").in(request.getState())));
            }
            if (!CollectionUtils.isEmpty(request.getType())) {
                predicates.add(cb.and(root.get("type").in(request.getType())));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        return users.map(user -> new UserDTO(user, false, false, false, false));
    }

    @Override
    public void changeUserState(ChangeUserStateRequest request) {
        // 查询用户
        User user = userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new WrongParamException(ResultEnum.USER_NOT_EXIST));
        // 修改状态
        user.setState(request.getNewState());
        // 落库
        userRepository.save(user);
    }
}
