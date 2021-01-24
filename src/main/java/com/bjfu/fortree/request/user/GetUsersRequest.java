package com.bjfu.fortree.request.user;

import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * 获取账号的请求
 * @author warthog
 */
@Data
public class GetUsersRequest {

    private Pagination pagination;

    private Filters filters;

    private Sorter sorter;
    @Data
    public static class Pagination {
        private Integer current;
        private Integer pageSize;
    }
    @Data
    public static class Filters {
        private List<String> account;
        private List<String> name;
        private List<String> organization;
        private List<UserStateEnum> state;
        private List<UserTypeEnum> type;
    }
    @Data
    public static class Sorter {
        private String columnKey;
        private String field;
        private String order;
    }
}
