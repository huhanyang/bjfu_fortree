package com.bjfu.fortree.service.impl;

import com.bjfu.fortree.pojo.dto.user.UserDTO;
import com.bjfu.fortree.pojo.dto.user.UserWithAuthoritiesAndWoodlandsDTO;
import com.bjfu.fortree.pojo.dto.user.UserWithAuthoritiesDTO;
import com.bjfu.fortree.pojo.entity.user.Authority;
import com.bjfu.fortree.pojo.entity.user.User;
import com.bjfu.fortree.pojo.entity.woodland.Woodland;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.exception.NotAllowedOperationException;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.exception.WrongParamException;
import com.bjfu.fortree.repository.user.AuthorityRepository;
import com.bjfu.fortree.repository.user.UserRepository;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import com.bjfu.fortree.pojo.request.user.*;
import com.bjfu.fortree.service.UserService;
import com.bjfu.fortree.pojo.vo.PageVO;
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
    private WoodlandRepository woodlandRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public UserWithAuthoritiesDTO loginCheck(LoginCheckRequest loginCheckRequest) {
        // 根据账号查询用户信息
        Optional<User> userOptional = userRepository.findByAccount(loginCheckRequest.getAccount());
        if(userOptional.isPresent()) {
            // 用户存在则检查密码是否匹配
            User user = userOptional.get();
            if(user.getPassword().equals(loginCheckRequest.getPassword())) {
                // 账号密码匹配则返回用户信息
                if(user.getState().equals(UserStateEnum.BANNED)) {
                    throw new NotAllowedOperationException(ResultEnum.ACCOUNT_BANNED);
                }
                return new UserWithAuthoritiesDTO(user);
            }
        }
        // 用户不存在或密码不匹配则返回null
        return null;
    }

    @Override
    @Transactional(rollbackOn = RuntimeException.class)
    public UserWithAuthoritiesDTO register(RegisterRequest registerRequest) {
        // 根据账号加锁查询用户信息
        Optional<User> userOptional = userRepository.findByAccountForUpdate(registerRequest.getAccount());
        if(userOptional.isEmpty()) {
            // 账号不存在则创建保存新用户
            User user = new User();
            user.setType(UserTypeEnum.USER);
            user.setState(UserStateEnum.ACTIVE);
            BeanUtils.copyProperties(registerRequest, user);
            user = userRepository.save(user);
            // 返回用户信息
            return new UserWithAuthoritiesDTO(user);
        } else {
            // 账号存在则返回null
            return null;
        }
    }

    @Override
    public UserWithAuthoritiesDTO getUserInfoWithAuthorities(String userAccount) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_NOT_EXIST);
        }
        User user = userOptional.get();
        return new UserWithAuthoritiesDTO(user);
    }

    @Override
    @Transactional(rollbackOn = RuntimeException.class)
    public UserDTO changePassword(String userAccount, ChangePasswordRequest changePasswordRequest) {
        // 根据账号加锁查询用户信息
        Optional<User> userOptional = userRepository.findByAccountForUpdate(userAccount);
        if(userOptional.isPresent()) {
            // 用户存在则检查密码是否匹配
            User user = userOptional.get();
            if(user.getPassword().equals(changePasswordRequest.getOldPassword())) {
                // 修改用户的密码并保存
                user.setPassword(changePasswordRequest.getNewPassword());
                user = userRepository.save(user);
                // 返回用户信息
                return new UserDTO(user);
            }
        }
        // 用户不存在或密码不匹配则返回null
        return null;
    }

    @Override
    public UserWithAuthoritiesAndWoodlandsDTO getUserWithAuthoritiesAndWoodlands(String userAccount) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        List<Woodland> woodlands = woodlandRepository.findByCreator(user);
        return new UserWithAuthoritiesAndWoodlandsDTO(user, woodlands);
    }

    @Override
    @Transactional(rollbackOn = RuntimeException.class)
    public UserWithAuthoritiesDTO grantUserAuthority(GrantUserAuthorityRequest grantUserAuthorityRequest) {
        // 根据账号加锁查询用户信息
        Optional<User> userOptional = userRepository.findByAccountForUpdate(grantUserAuthorityRequest.getAccount());
        if(userOptional.isEmpty()) {
            // 用户不存在则抛出参数错误异常
            throw new WrongParamException(ResultEnum.PARAM_WRONG);
        }
        User user = userOptional.get();
        List<Authority> userAuthorities = user.getAuthorities();
        // 保存用户已经拥有的权限
        Map<AuthorityTypeEnum, Authority> existAuthorities = userAuthorities.stream()
                .collect(Collectors.toMap(Authority::getType, authority -> authority));
        // 系统中所有存在的权限
        Set<String> authorityTypeEnums =
                Arrays.stream(AuthorityTypeEnum.values()).map(Enum::name).collect(Collectors.toSet());
        // 将授权请求中的权限字符串通过权限列表过滤出系统中存在的权限 并过滤掉用户已经拥有的权限 将剩下的权限保存
        Arrays.stream(grantUserAuthorityRequest.getAuthorities())
                .filter(authorityTypeEnums::contains)
                .map(AuthorityTypeEnum::valueOf)
                .filter(type -> !existAuthorities.containsKey(type))
                .forEach(type -> {
                    Authority authority = new Authority();
                    authority.setUser(userOptional.get());
                    authority.setType(type);
                    userAuthorities.add(authority);
                });
        // 保存新的权限列表并返回新的用户信息
        user = userRepository.save(user);
        return new UserWithAuthoritiesDTO(user);
    }

    @Override
    @Transactional(rollbackOn = RuntimeException.class)
    public UserWithAuthoritiesDTO revokeUserAuthority(RevokeUserAuthorityRequest revokeUserAuthorityRequest) {
        // 根据账号加锁查询用户信息
        Optional<User> userOptional = userRepository.findByAccountForUpdate(revokeUserAuthorityRequest.getAccount());
        if(userOptional.isEmpty()) {
            // 用户不存在则抛出参数错误异常
            throw new WrongParamException(ResultEnum.PARAM_WRONG);
        }
        User user = userOptional.get();
        List<Authority> userAuthorities = user.getAuthorities();
        // 保存用户已经拥有的权限
        Map<AuthorityTypeEnum, Authority> existAuthorities = userAuthorities.stream()
                .collect(Collectors.toMap(Authority::getType, authority -> authority));
        // 系统中所有存在的权限
        Set<String> authorityTypeEnums =
                Arrays.stream(AuthorityTypeEnum.values()).map(Enum::name).collect(Collectors.toSet());
        // 将请求中的权限字符串通过权限列表过滤出系统中存在的权限 并过滤掉用户未曾拥有的权限 将剩下的权限依次删除
        Arrays.stream(revokeUserAuthorityRequest.getAuthorities())
                .filter(authorityTypeEnums::contains)
                .map(AuthorityTypeEnum::valueOf)
                .filter(existAuthorities::containsKey)
                .forEach(type -> {
                    userAuthorities.remove(existAuthorities.get(type));
                    authorityRepository.deleteByUserAndType(user, type);
                });
        // 保存新的权限列表
        userRepository.save(user);
        return new UserWithAuthoritiesDTO(user);
    }

    @Override
    public PageVO<UserDTO> getUsers(GetUsersRequest getUsersRequest) {
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
        List<UserDTO> userDTOS = users.getContent().stream().map(UserDTO::new).collect(Collectors.toList());
        return new PageVO<>(users.getTotalElements(), userDTOS);
    }

    @Override
    public UserDTO changeUserState(ChangeUserStateRequest changeUserStateRequest) {
        Optional<User> userOptional = userRepository.findByAccount(changeUserStateRequest.getAccount());
        if(userOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.PARAM_WRONG);
        }
        User user = userOptional.get();
        user.setState(changeUserStateRequest.getNewState());
        user = userRepository.save(user);
        return new UserDTO(user);
    }
}
