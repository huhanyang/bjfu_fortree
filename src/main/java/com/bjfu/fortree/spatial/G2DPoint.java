package com.bjfu.fortree.spatial;

import lombok.Data;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.crs.CrsRegistry;

import javax.validation.constraints.NotNull;

/**
 * G2D的点
 * @author warthog
 */
@Data
public class G2DPoint {

    public G2DPoint() {
    }
    public G2DPoint(Point<G2D> point) {
        this.longitude = point.getPosition().getLon();
        this.latitude = point.getPosition().getLat();
    }
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

    public static Point<G2D> convertToGeom(G2DPoint g2DPoint) {
        G2D g2D = new G2D(g2DPoint.getLongitude(), g2DPoint.getLatitude());
        return new Point(g2D, CrsRegistry.getCoordinateReferenceSystemForEPSG(4326, null));
    }

}
