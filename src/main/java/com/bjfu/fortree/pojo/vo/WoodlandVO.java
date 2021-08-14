package com.bjfu.fortree.pojo.vo;

import com.bjfu.fortree.enums.entity.WoodlandShapeEnum;
import com.bjfu.fortree.pojo.dto.WoodlandDTO;
import com.bjfu.fortree.spatial.G2dPoint;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class WoodlandVO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 修改时间
     */
    private Date lastModifiedTime;
    /**
     * 样地名称
     */
    private String name;
    /**
     * 创建人
     */
    private UserVO creator;
    /**
     * 样地地址
     */
    private String address;
    /**
     * 样地中心经纬度
     */
    private G2dPoint position;
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
     * 形状
     */
    private WoodlandShapeEnum shape;
    /**
     * 长度 米
     */
    private Double length;
    /**
     * 宽度 米
     */
    private Double width;
    /**
     * 附加信息(JSON)
     */
    private String addition;
    /**
     * 创建的记录
     */
    private List<RecordVO> records = new ArrayList<>();

    public WoodlandVO(WoodlandDTO woodlandDTO) {
        if (woodlandDTO != null) {
            BeanUtils.copyProperties(woodlandDTO, this);
            this.creator = Optional.ofNullable(woodlandDTO.getCreator()).map(UserVO::new).orElse(null);
            this.records = Optional.ofNullable(woodlandDTO.getRecords())
                    .map(recordDTOS -> recordDTOS.stream().map(RecordVO::new).collect(Collectors.toList()))
                    .orElse(null);
        }
    }
}
