package com.bjfu.fortree.pojo.vo.woodland;

import com.bjfu.fortree.pojo.dto.woodland.WoodlandDetailDTO;
import com.bjfu.fortree.enums.entity.WoodlandShapeEnum;
import com.bjfu.fortree.spatial.G2dPoint;
import com.bjfu.fortree.pojo.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author warthog
 */
@Data
public class WoodlandDetailVO {
    public WoodlandDetailVO(WoodlandDetailDTO woodlandDetailDTO) {
        BeanUtils.copyProperties(woodlandDetailDTO, this, "creator", "records");
        this.setCreator(new UserVO(woodlandDetailDTO.getCreator()));
        this.setRecords(woodlandDetailDTO.getRecords().stream().map(RecordVO::new).collect(Collectors.toList()));
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
    private List<RecordVO> records;

}
