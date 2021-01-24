package com.bjfu.fortree.vo.woodland;

import com.bjfu.fortree.dto.woodland.WoodlandDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class WoodlandVO {
    public WoodlandVO(WoodlandDTO woodlandDTO) {
        BeanUtils.copyProperties(woodlandDTO, this);
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
