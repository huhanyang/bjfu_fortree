package com.bjfu.fortree.spatial;

import lombok.Data;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Polygon;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

/**
 * 2维的多边形
 * @author warthog
 */
@Data
public class G2dPolygon {

    public G2dPolygon() {}

    public G2dPolygon(List<G2dPoint> g2dPointList) {
        this.g2dPointList = g2dPointList;
    }

    public G2dPolygon(Polygon<G2D> polygon) {
        List<G2dPoint> list = new ArrayList<>();
        Arrays.stream(polygon.components())
                .findFirst()
                .ifPresent(g2dLinearRing -> g2dLinearRing.getPositions().forEach(g2D -> list.add(new G2dPoint(g2D.getLon(), g2D.getLat()))));
        this.g2dPointList = list;
    }

    /**
     * 点集合
     */
    @NotEmpty(message = "点集合不能为空")
    private List<G2dPoint> g2dPointList;

    public static Polygon<G2D> convertToGeom(G2dPolygon g2dPolygon) {
        G2D[] g2dS = g2dPolygon.getG2dPointList()
                .stream()
                .map(g2dPoint -> g(g2dPoint.getLongitude(), g2dPoint.getLatitude()))
                .toArray(G2D[]::new);
        return polygon(WGS84, ring(g2dS));
    }

    public Polygon<G2D> convertToGeom() {
        return convertToGeom(this);
    }
}
