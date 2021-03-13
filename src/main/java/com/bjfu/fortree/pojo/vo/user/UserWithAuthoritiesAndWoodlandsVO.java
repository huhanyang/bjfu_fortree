package com.bjfu.fortree.pojo.vo.user;

import com.bjfu.fortree.pojo.dto.user.UserWithAuthoritiesAndWoodlandsDTO;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.pojo.vo.woodland.WoodlandVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserWithAuthoritiesAndWoodlandsVO {
    public UserWithAuthoritiesAndWoodlandsVO(UserWithAuthoritiesAndWoodlandsDTO user) {
        BeanUtils.copyProperties(user, this,
                "authorities", "woodlands");
        this.authorities = user.getAuthorities().stream()
                .map(AuthorityVO::new)
                .collect(Collectors.toList());
        this.woodlands = user.getWoodlands().stream()
                .map(WoodlandVO::new)
                .collect(Collectors.toList());
    }
    /**
     * 用户名
     */
    private String account;
    /**
     * 姓名
     */
    private String name;
    /**
     * 所属组织名
     */
    private String organization;
    /**
     * 注册时间
     */
    private Date createdTime;
    /**
     * 账号类型
     */
    private UserTypeEnum type;
    /**
     * 拥有的权限
     */
    private List<AuthorityVO> authorities;
    /**
     * 创建的林地
     */
    private List<WoodlandVO> woodlands;
}
