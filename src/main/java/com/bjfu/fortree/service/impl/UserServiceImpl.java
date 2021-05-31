package com.bjfu.fortree.service.impl;

import com.bjfu.fortree.exception.BizException;
import com.bjfu.fortree.pojo.dto.user.UserDTO;
import com.bjfu.fortree.pojo.entity.user.Authority;
import com.bjfu.fortree.pojo.entity.user.User;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.exception.WrongParamException;
import com.bjfu.fortree.repository.user.AuthorityRepository;
import com.bjfu.fortree.repository.user.UserRepository;
import com.bjfu.fortree.pojo.request.user.*;
import com.bjfu.fortree.service.UserService;
import com.bjfu.fortree.util.EncryptionUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author warthog
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public UserDTO login(LoginCheckRequest request) {
        // 根据账号查询用户信息
        User user = userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new BizException(ResultEnum.ACCOUNT_NOT_EXIST_OR_PASSWORD_WRONG));
        // 判断密码是否匹配
        if(!user.getPassword().equals(EncryptionUtil.md5Encode(request.getPassword()))) {
            throw new BizException(ResultEnum.PASSWORD_WRONG);
        }
        // 判断用户是否被封禁
        if(user.getState().equals(UserStateEnum.BANNED)) {
            throw new BizException(ResultEnum.ACCOUNT_BANNED);
        }
        // 返回用户信息
        return new UserDTO(user);
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
        user.setState(UserStateEnum.ACTIVE);
        BeanUtils.copyProperties(request, user);
        // 落库
        userRepository.save(user);
        // 返回用户信息
        return new UserDTO(user);
    }

    @Override
    public UserDTO getInfo(String userAccount) {
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        return new UserDTO(user);
    }

    @Override
    @Transactional
    public void changePassword(String userAccount, ChangePasswordRequest request) {
        // 根据账号加锁查询用户信息
        User user = userRepository.findByAccountForUpdate(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 密码验证
        if(!user.getPassword().equals(EncryptionUtil.md5Encode(request.getOldPassword()))) {
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
        authorityRepository.deleteAll(needDeleteAuthorities);
    }

    @Override
    public Page<UserDTO> getUsers(GetUsersRequest getUsersRequest) {
        PageRequest pageRequest = PageRequest.of(getUsersRequest.getCurrent() - 1, getUsersRequest.getPageSize());
        if(getUsersRequest.getField() != null) {
            Sort sort = Sort.by(new Sort.Order(getUsersRequest.getOrder(), getUsersRequest.getField()));
            pageRequest = PageRequest.of(getUsersRequest.getCurrent() - 1, getUsersRequest.getPageSize(), sort);
        }
        Page<User> users = userRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!CollectionUtils.isEmpty(getUsersRequest.getAccount())) {
                predicates.add(cb.like(root.get("account"), "%" + getUsersRequest.getAccount().get(0) + "%"));
            }
            if (!CollectionUtils.isEmpty(getUsersRequest.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + getUsersRequest.getName().get(0) + "%"));
            }
            if (!CollectionUtils.isEmpty(getUsersRequest.getOrganization())) {
                predicates.add(cb.like(root.get("organization"), "%" + getUsersRequest.getOrganization().get(0) + "%"));
            }
            if (!CollectionUtils.isEmpty(getUsersRequest.getState())) {
                predicates.add(cb.and(root.get("state").in(getUsersRequest.getState())));
            }
            if (!CollectionUtils.isEmpty(getUsersRequest.getType())) {
                predicates.add(cb.and(root.get("type").in(getUsersRequest.getType())));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        }, pageRequest);
        return users.map(UserDTO::new);
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
