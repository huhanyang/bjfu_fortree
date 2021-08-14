package com.bjfu.fortree.pojo.request.user;

import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.pojo.request.BasePageAndSorterRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 获取账号的请求
 *
 * @author warthog
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetUsersRequest extends BasePageAndSorterRequest {
    /**
     * 按照账号模糊搜素
     */
    @Nullable
    private String account;
    /**
     * 按照姓名模糊搜素
     */
    @Nullable
    private String name;
    /**
     * 按照组织模糊搜素
     */
    @Nullable
    private String organization;
    /**
     * 按照状态匹配
     */
    @Nullable
    private List<UserStateEnum> state;
    /**
     * 按照类型匹配
     */
    @Nullable
    private List<UserTypeEnum> type;
}
