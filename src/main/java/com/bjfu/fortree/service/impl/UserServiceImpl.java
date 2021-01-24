package com.bjfu.fortree.service.impl;

import com.bjfu.fortree.dto.user.UserDTO;
import com.bjfu.fortree.dto.user.UserWithAuthoritiesAndWoodlandsDTO;
import com.bjfu.fortree.dto.user.UserWithAuthoritiesDTO;
import com.bjfu.fortree.entity.user.Authority;
import com.bjfu.fortree.entity.user.User;
import com.bjfu.fortree.entity.woodland.Woodland;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.exception.WrongParamException;
import com.bjfu.fortree.repository.user.UserRepository;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import com.bjfu.fortree.request.user.*;
import com.bjfu.fortree.service.UserService;
import com.bjfu.fortree.vo.PageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

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

    @Override
    public UserWithAuthoritiesDTO loginCheck(LoginCheckRequest loginCheckRequest) {
        // 根据账号查询用户信息
        Optional<User> userOptional = userRepository.findByAccount(loginCheckRequest.getAccount());
        if(userOptional.isPresent()) {
            // 用户存在则检查密码是否匹配
            User user = userOptional.get();
            if(user.getPassword().equals(loginCheckRequest.getPassword())) {
                // 账号密码匹配则返回用户信息
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
            throw new SystemWrongException(ResultEnum.SESSION_USER_NOT_EXIST);
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
                .forEach(type -> userAuthorities.remove(existAuthorities.get(type)));
        // 保存新的权限列表并返回新的用户信息
        user = userRepository.save(user);
        return new UserWithAuthoritiesDTO(user);
    }

    @Override
    public PageVO<UserDTO> getUsers(GetUsersRequest getUsersRequest) {

        User exampleUser = new User();
        GetUsersRequest.Filters filters = getUsersRequest.getFilters();
        if(filters.getAccount()!=null && filters.getAccount().size() > 0) {
            Optional<String> account = filters.getAccount().stream().findFirst();
            exampleUser.setAccount(account.isEmpty()?null: account.get());
        }
        if(filters.getName()!=null && filters.getName().size() > 0) {
            Optional<String> name = filters.getName().stream().findFirst();
            exampleUser.setName(name.isEmpty()?null: name.get());
        }
        if(filters.getOrganization()!=null && filters.getOrganization().size() > 0) {
            Optional<String> organization = filters.getOrganization().stream().findFirst();
            exampleUser.setOrganization(organization.isEmpty()?null: organization.get());
        }
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase().withIgnoreNullValues();
        Example<User> example = Example.of(exampleUser, matcher);

        GetUsersRequest.Pagination pagination = getUsersRequest.getPagination();
        GetUsersRequest.Sorter sorter = getUsersRequest.getSorter();
        PageRequest pageRequest = PageRequest.of(pagination.getCurrent() - 1, pagination.getPageSize());
        if(sorter != null && sorter.getOrder() != null) {
            Sort.Direction direction = Sort.Direction.ASC;
            if("descend".equals(sorter.getOrder())) {
                direction = Sort.Direction.DESC;
            }
            Sort sort = Sort.by(new Sort.Order(direction, sorter.getColumnKey()));
            pageRequest = PageRequest.of(pagination.getCurrent(), pagination.getPageSize(), sort);
        }

        Page<User> userPage = userRepository.findAll(example, pageRequest);
        List<UserDTO> users = userPage.get()
                .filter(user -> (filters.getState() == null || filters.getState().size() == 0 || filters.getState().contains(user.getState())) &&
                        (filters.getType() == null || filters.getType().size() == 0 || filters.getType().contains(user.getType())))
                .map(UserDTO::new)
                .collect(Collectors.toList());

        long count = userPage.getTotalElements();
        PageVO<UserDTO> userDTOPageVO = new PageVO<>();
        userDTOPageVO.setCount(count);
        userDTOPageVO.setContents(users);

        return userDTOPageVO;
    }
}
