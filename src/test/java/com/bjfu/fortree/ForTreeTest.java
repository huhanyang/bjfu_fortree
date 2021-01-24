package com.bjfu.fortree;

import com.bjfu.fortree.dto.user.UserDTO;
import com.bjfu.fortree.enums.entity.UserStateEnum;
import com.bjfu.fortree.enums.entity.UserTypeEnum;
import com.bjfu.fortree.request.user.GetUsersRequest;
import com.bjfu.fortree.service.UserService;
import com.bjfu.fortree.vo.PageVO;
import org.geolatte.geom.Point;
import org.geolatte.geom.codec.Wkt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

@SpringBootTest
public class ForTreeTest {

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
    }

    @Autowired
    private UserService userService;

    @Test
    public void testE() {
        System.out.println("start---------------------------------------");
        GetUsersRequest getUsersRequest = new GetUsersRequest();
        GetUsersRequest.Pagination pagination = new GetUsersRequest.Pagination();

        pagination.setCurrent(0);
        pagination.setPageSize(20);

        GetUsersRequest.Filters filters = new GetUsersRequest.Filters();
        //filters.setName("测试");
        filters.setState(Collections.singletonList(UserStateEnum.ACTIVE));
        filters.setType(Collections.singletonList(UserTypeEnum.USER));
        getUsersRequest.setFilters(filters);
        getUsersRequest.setPagination(pagination);
        getUsersRequest.setSorter(null);
        PageVO<UserDTO> users = userService.getUsers(getUsersRequest);
        System.out.println(users);
    }

    /**
     * Failed to create query for method
     * public abstract org.springframework.data.domain.Page com.bjfu.fortree.repository.user.
     * UserRepository.findAllByTypeInAndStateIn(org.springframework.data.domain.Example,
     * com.bjfu.fortree.enums.entity.UserTypeEnum[],
     * com.bjfu.fortree.enums.entity.UserStateEnum[],
     * org.springframework.data.domain.Pageable)!
     *
     * Operator IN on type requires a Collection argument,
     * found interface org.springframework.data.domain.Example in method
     * public abstract org.springframework.data.domain.Page
     * com.bjfu.fortree.repository.user.UserRepository.findAllByTypeInAndStateIn(org.springframework.data.domain.Example,com.bjfu.fortree.enums.entity.UserTypeEnum[],com.bjfu.fortree.enums.entity.UserStateEnum[],org.springframework.data.domain.Pageable)
     */

}
