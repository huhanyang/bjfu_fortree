package com.bjfu.fortree.pojo.dto;

import com.bjfu.fortree.enums.entity.WoodlandShapeEnum;
import com.bjfu.fortree.pojo.entity.Woodland;
import com.bjfu.fortree.spatial.G2dPoint;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author warthog
 */
@Data
public class WoodlandDTO {

    public WoodlandDTO(Woodland woodland, Boolean needCreator, Boolean needRecords) {
        if(woodland != null) {
            BeanUtils.copyProperties(woodland, this, "creator", "position", "records");
            this.setPosition(new G2dPoint(woodland.getPosition()));
            if(needCreator) {
                this.setCreator(new UserDTO(woodland.getCreator(), false, false, false, false));
            }
            if(needRecords) {
                this.setRecords(woodland.getRecords().stream().map(record -> new RecordDTO(record, false, true, false)).collect(Collectors.toList()));
            }
        }
    }

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
    private UserDTO creator;
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
    private List<RecordDTO> records = new ArrayList<>();
}
