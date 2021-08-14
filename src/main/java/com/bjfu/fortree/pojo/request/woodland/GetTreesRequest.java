package com.bjfu.fortree.pojo.request.woodland;

import com.bjfu.fortree.pojo.request.BasePageAndSorterRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * @author warthog
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetTreesRequest extends BasePageAndSorterRequest {
    /**
     * 记录id
     */
    @NotNull(message = "记录id不能为空")
    private Long recordId;
    /**
     * 按树木id模糊搜索
     */
    @Nullable
    private String treeId;
    /**
     * 按树种模糊搜索
     */
    @Nullable
    private String species;
}
