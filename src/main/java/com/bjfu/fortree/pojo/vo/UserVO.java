package com.bjfu.fortree.pojo.vo;

import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.pojo.dto.UserDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class UserVO {

    public UserVO(UserDTO userDTO) {
        if(userDTO != null) {
            BeanUtils.copyProperties(userDTO, this);
            this.authorities = Optional.ofNullable(userDTO.getAuthorities())
                    .map(authorityDTOS -> authorityDTOS.stream().map(AuthorityVO::new).collect(Collectors.toList()))
                    .orElse(null);
            this.woodlands = Optional.ofNullable(userDTO.getWoodlands())
                    .map(woodlandDTOS -> woodlandDTOS.stream().map(WoodlandVO::new).collect(Collectors.toList()))
                    .orElse(null);
            this.records = Optional.ofNullable(userDTO.getRecords())
                    .map(recordDTOS -> recordDTOS.stream().map(RecordVO::new).collect(Collectors.toList()))
                    .orElse(null);
            this.applyJobs = Optional.ofNullable(userDTO.getApplyJobs())
                    .map(applyJobDTOS -> applyJobDTOS.stream().map(ApplyJobVO::new).collect(Collectors.toList()))
                    .orElse(null);
        }
    }

    public UserVO(UserDTO userDTO, String token) {
        this(userDTO);
        this.token = token;
    }

    /**
     * 主键
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 修改时间
     */
    private Date lastModifiedTime;
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
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 账号类型
     */
    private UserTypeEnum type;

    /**
     * 账号状态
     */
    private UserStateEnum state;

    /**
     * 拥有的权限
     */
    private List<AuthorityVO> authorities = new ArrayList<>();

    /**
     * 创建的林地
     */
    private List<WoodlandVO> woodlands = new ArrayList<>();

    /**
     * 创建的记录
     */
    private List<RecordVO> records = new ArrayList<>();

    /**
     * 申请
     */
    private List<ApplyJobVO> applyJobs = new ArrayList<>();

    /**
     * 登录token
     */
    private String token;
}
