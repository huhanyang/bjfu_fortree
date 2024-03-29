package com.bjfu.fortree.pojo.request.woodland;

import com.bjfu.fortree.pojo.request.BasePageAndSorterRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.annotation.Nullable;

/**
 * @author warthog
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetWoodlandsRequest extends BasePageAndSorterRequest {
    /**
     * 按林地名模糊搜索
     */
    @Nullable
    private String name;
    /**
     * 按国家名模糊搜索
     */
    @Nullable
    private String country;
    /**
     * 按省名模糊搜索
     */
    @Nullable
    private String province;
    /**
     * 按城市名模糊搜索
     */
    @Nullable
    private String city;
}
