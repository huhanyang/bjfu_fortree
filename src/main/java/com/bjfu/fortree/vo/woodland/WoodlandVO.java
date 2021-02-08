package com.bjfu.fortree.vo.woodland;

import com.bjfu.fortree.dto.user.UserDTO;
import com.bjfu.fortree.dto.woodland.WoodlandDTO;
import com.bjfu.fortree.vo.user.UserVO;
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
