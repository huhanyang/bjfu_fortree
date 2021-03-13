package com.bjfu.fortree.pojo.request.woodland;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author warthog
 */
@Data
public class GetWoodlandsInRectangleBoundsRequest {
    @NotNull(message = "地图视野东北边缘点经度不能为空")
    private Double neLng;
    @NotNull(message = "地图视野东北边缘点维度不能为空")
    private Double neLat;
    @NotNull(message = "地图视野西南边缘点经度不能为空")
    private Double swLng;
    @NotNull(message = "地图视野西南边缘点维度不能为空")
    private Double swLat;
}
