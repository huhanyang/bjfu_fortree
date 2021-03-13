package com.bjfu.fortree.pojo.dto.woodland;

import com.bjfu.fortree.pojo.dto.user.UserDTO;
import com.bjfu.fortree.pojo.entity.woodland.Woodland;
import com.bjfu.fortree.spatial.G2dPoint;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author warthog
 */
@Data
public class WoodlandDTO {
    public WoodlandDTO(Woodland woodland) {
        BeanUtils.copyProperties(woodland, this, "creator");
        this.setCreator(new UserDTO(woodland.getCreator()));
        this.setPosition(new G2dPoint(woodland.getPosition()));
    }
    /**
     * 样地id
     */
    private Long id;
    /**
     * 样地名称
     */
    private String name;
    /**
     * 样地中心经纬度
     */
    private G2dPoint position;
    /**
     * 创建人
     */
    private UserDTO creator;
    /**
     * 国家
     */
    private String country;
    /**
     * 省/州
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 样地地址
     */
    private String address;
}
