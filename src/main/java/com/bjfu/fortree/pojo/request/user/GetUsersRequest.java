package com.bjfu.fortree.pojo.request.user;

import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import lombok.Data;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 获取账号的请求
 * @author warthog
 */
@Data
public class GetUsersRequest {
    //分页
    @NotNull(message = "分页当前页数不能为空")
    private Integer current;
    @NotNull(message = "分页每页的数量不能为空")
    private Integer pageSize;
    //过滤
    private List<String> account;
    private List<String> name;
    private List<String> organization;
    private List<UserStateEnum> state;
    private List<UserTypeEnum> type;
    //排序
    private String field;
    private Sort.Direction order;
}
