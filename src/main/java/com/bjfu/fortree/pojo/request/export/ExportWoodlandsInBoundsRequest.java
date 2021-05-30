package com.bjfu.fortree.pojo.request.export;

import com.bjfu.fortree.spatial.G2dPolygon;
import lombok.Data;

/**
 * @author warthog
 */
@Data
public class ExportWoodlandsInBoundsRequest {
    private G2dPolygon polygon;
}
