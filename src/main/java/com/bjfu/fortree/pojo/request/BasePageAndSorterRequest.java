package com.bjfu.fortree.pojo.request;

import lombok.Data;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 分页及单属性排序请求基类
 *
 * @author warthog
 */
@Data
public abstract class BasePageAndSorterRequest {

    /**
     * 分页
     */
    @NotNull(message = "分页参数不能为空")
    private Pagination pagination;
    /**
     * 排序
     */
    private List<Sorter> sorter;

    @Data
    public static class Pagination {
        /**
         * 当前页数（从1开始）
         */
        @NotNull(message = "当前页数不能为空")
        private Integer current;
        /**
         * 每页的数量
         */
        @NotNull(message = "每页的数量不能为空")
        private Integer pageSize;
    }

    @Data
    public static class Sorter {
        /**
         * 排序的字段
         */
        private String field;
        /**
         * 排序的顺序
         */
        private Sort.Direction order;
    }

}

