package com.bjfu.fortree.pojo.vo.woodland;

import com.bjfu.fortree.pojo.dto.woodland.WoodlandDTO;
import com.bjfu.fortree.spatial.G2dPoint;
import com.bjfu.fortree.pojo.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author warthog
 */
@Data
public class WoodlandVO {
    public WoodlandVO(WoodlandDTO woodlandDTO) {
        BeanUtils.copyProperties(woodlandDTO, this, "creator");
        this.setCreator(new UserVO(woodlandDTO.getCreator()));
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
    private UserVO creator;
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
