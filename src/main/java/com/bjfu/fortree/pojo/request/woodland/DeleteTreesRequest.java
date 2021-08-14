package com.bjfu.fortree.pojo.request.woodland;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 删除记录中树木的请求
 *
 * @author warthog
 */
@Data
public class DeleteTreesRequest {
    /**
     * 记录id
     */
    @NotNull(message = "林地记录id不能为空")
    private Long recordId;
    /**
     * 树木位置
     */
    @NotEmpty(message = "删除的树木id不能为空")
    private List<Long> treeIds;
}
