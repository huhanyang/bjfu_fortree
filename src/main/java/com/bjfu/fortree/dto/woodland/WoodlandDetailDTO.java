package com.bjfu.fortree.dto.woodland;

import com.bjfu.fortree.dto.user.UserDTO;
import com.bjfu.fortree.entity.woodland.Woodland;
import com.bjfu.fortree.enums.entity.WoodlandShapeEnum;
import com.bjfu.fortree.spatial.G2DPoint;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author warthog
 */
@Data
public class WoodlandDetailDTO {

    public WoodlandDetailDTO(Woodland woodland) {
        BeanUtils.copyProperties(woodland, this, "creator", "position", "records");
        this.setCreator(new UserDTO(woodland.getCreator()));
        this.setPosition(new G2DPoint(woodland.getPosition()));
        this.setRecords(woodland.getRecords().stream().map(RecordDTO::new).collect(Collectors.toList()));
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
    private G2DPoint position;
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
    private List<RecordDTO> records;
}
