package com.bjfu.fortree.dto.woodland;

import com.bjfu.fortree.entity.woodland.Woodland;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class WoodlandDTO {

    public WoodlandDTO(Woodland woodland) {
        BeanUtils.copyProperties(woodland, this);
    }

    /**
     * 样地名称
     */
    private String name;
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
