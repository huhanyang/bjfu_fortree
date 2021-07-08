package com.bjfu.fortree.pojo.request.woodland;

import lombok.Data;

import javax.annotation.Nullable;

@Data
public class GetAllWoodlandsRequest {
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
    /**
     * 林地面积
     */
    @Nullable
    private Double area;
    /**
     * 林地面积方向
     */
    @Nullable
    private NumberDirection areaDirection;
    /**
     * 树木总数
     */
    @Nullable
    private Integer treeCount;
    /**
     * 树木总数方向
     */
    @Nullable
    private NumberDirection treeCountDirection;
    /**
     * 平均树高
     */
    @Nullable
    private Double treeMeanHeight;
    /**
     * 平均树高方向
     */
    @Nullable
    private NumberDirection treeMeanHeightDirection;

    /**
     * 数值型方向
     */
    public enum NumberDirection {
        MAX, MIN
    }

}
