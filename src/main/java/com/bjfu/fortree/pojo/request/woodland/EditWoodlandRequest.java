package com.bjfu.fortree.pojo.request.woodland;

import com.bjfu.fortree.enums.entity.WoodlandShapeEnum;
import com.bjfu.fortree.spatial.G2dPoint;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 编辑林地信息请求
 * @author warthog
 */
@Data
public class EditWoodlandRequest {
    /**
     * 林地id
     */
    @NotNull(message = "林地的id不能为空")
    Long woodlandId;
    /**
     * 样地名称
     */
    @NotBlank(message = "样地名称不能为空")
    @Length(min = 1, max = 32, message = "样地名称长度1-32个字符")
    private String name;
    /**
     * 样地地址
     */
    @NotBlank(message = "样地地址不能为空")
    @Length(min = 1, max = 32, message = "样地地址长度1-32个字符")
    private String address;
    /**
     * 样地中心经纬度
     */
    @NotNull(message = "样地中心位置经维度不能为空")
    G2dPoint position;
    /**
     * 国家
     */
    @NotBlank(message = "样地所在国不能为空")
    @Length(min = 1, max = 32, message = "样地所在国长度1-32个字符")
    private String country;
    /**
     * 省/州
     */
    @NotBlank(message = "样地所在省不能为空")
    @Length(min = 1, max = 32, message = "样地所在省长度1-32个字符")
    private String province;
    /**
     * 城市
     */
    @NotBlank(message = "样地所在城市不能为空")
    @Length(min = 1, max = 32, message = "样地所在城市长度1-32个字符")
    private String city;
    /**
     * 形状
     */
    @NotNull(message = "样地形状不能为空")
    private WoodlandShapeEnum shape;
    /**
     * 长度
     */
    @NotNull(message = "样地长度不能为空")
    private Double length;
    /**
     * 宽度
     */
    @NotNull(message = "样地宽度不能为空")
    private Double width;
    /**
     * 附加信息(JSON)
     */
    @Length(max = 512, message = "附加信息长度最大512个字符")
    private String addition;
}
