package com.bjfu.fortree.spatial;

import lombok.Data;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

import javax.validation.constraints.NotNull;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

/**
 * 2维的点
 *
 * @author warthog
 */
@Data
public class G2dPoint {

    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    private double longitude;
    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    private double latitude;

    public G2dPoint() {
    }

    public G2dPoint(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public G2dPoint(Point<G2D> point) {
        this.longitude = point.getPosition().getLon();
        this.latitude = point.getPosition().getLat();
    }

    public static Point<G2D> convertToGeom(G2dPoint g2dPoint) {
        return point(WGS84, g(g2dPoint.getLongitude(), g2dPoint.getLatitude()));
    }

    public Point<G2D> convertToGeom() {
        return convertToGeom(this);
    }

    @Override
    public String toString() {
        return "G2dPoint{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
