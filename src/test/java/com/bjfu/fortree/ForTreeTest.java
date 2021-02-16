package com.bjfu.fortree;

import com.bjfu.fortree.dto.user.UserDTO;
import com.bjfu.fortree.entity.woodland.Woodland;
import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import com.bjfu.fortree.request.user.GetUsersRequest;
import com.bjfu.fortree.request.woodland.AddRecordRequest;
import com.bjfu.fortree.service.UserService;
import com.bjfu.fortree.util.ResponseUtil;
import com.bjfu.fortree.vo.PageVO;
import org.checkerframework.checker.units.qual.A;
import org.geolatte.geom.*;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.crs.CrsRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

@SpringBootTest
public class ForTreeTest {

    @Autowired
    private WoodlandRepository woodlandRepository;

    @Test
    public void test() {
//        LocationEntity tree = new LocationEntity();
//        Iterable<LocationEntity> all = locationEntityRepository.findAll();
//        String pointStr1  =  "SRID=4326;POINT(115 35)";
//        Point decode =(Point) Wkt.newDecoder().decode(pointStr1);
//        System.out.println(all);
//        SET @g = 'POLYGON((0 0,10 0,10 10,0 10,0 0))';
//        SELECT st_contains(ST_POLYGONFROMTEXT(@g, 4326),
//        m.point) AS res
//        FROM location_entity m
        Double neLng= -137.87641394333292;
        Double neLat= 67.21631755073415;
        Double swLng= 171.9075967579356;
        Double swLat= 58.329978614853935;
        PositionSequence<G2D> wgs84positionSequence =
                PositionSequenceBuilders.fixedSized(5, G2D.class)
                        .add(new G2D(neLng, neLat))
                        .add(new G2D(neLng, swLat))
                        .add(new G2D(swLng, swLat))
                        .add(new G2D(swLng, neLat))
                        .add(new G2D(neLng, neLat))
                        .toPositionSequence();
        Polygon<G2D> polygon = new Polygon(wgs84positionSequence, CrsRegistry.getCoordinateReferenceSystemForEPSG(4326, null));
        List<Woodland> woodlandsInBound = woodlandRepository.findWoodlandsInPolygon(polygon);
        System.out.println(woodlandsInBound);
    }


}
