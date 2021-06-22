package com.bjfu.fortree.pojo.dto;

import com.bjfu.fortree.pojo.entity.*;
import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDTO {

    public UserDTO(User user, Boolean needAuthorities, Boolean needWoodlands, Boolean needRecords, Boolean needApplyJobs) {
        if(user != null) {
            BeanUtils.copyProperties(user, this, "authorities", "woodlands", "records", "applyJobs");
            if(needAuthorities) {
                this.authorities = user.getAuthorities().stream().map(authority -> new AuthorityDTO(authority, false)).collect(Collectors.toList());
            }
            if(needWoodlands) {
                this.woodlands = user.getWoodlands().stream().map(woodland -> new WoodlandDTO(woodland, true, false)).collect(Collectors.toList());
            }
            if(needRecords) {
                this.records = user.getRecords().stream().map(record -> new RecordDTO(record, true, false, false)).collect(Collectors.toList());
            }
            if(needApplyJobs) {
                this.applyJobs = user.getApplyJobs().stream().map(applyJob -> new ApplyJobDTO(applyJob, false, false, false, true)).collect(Collectors.toList());
            }
        }
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
     * 密码
     */
    private String password;
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
    private List<AuthorityDTO> authorities = new ArrayList<>();

    /**
     * 创建的林地
     */
    private List<WoodlandDTO> woodlands = new ArrayList<>();

    /**
     * 创建的记录
     */
    private List<RecordDTO> records = new ArrayList<>();

    /**
     * 申请
     */
    private List<ApplyJobDTO> applyJobs = new ArrayList<>();
}
