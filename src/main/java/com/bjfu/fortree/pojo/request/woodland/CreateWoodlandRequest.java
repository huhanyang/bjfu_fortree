package com.bjfu.fortree.pojo.request.woodland;

import com.bjfu.fortree.approval.ApprovalRequest;
import com.bjfu.fortree.enums.entity.WoodlandShapeEnum;
import com.bjfu.fortree.spatial.G2dPoint;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * 创建林地请求
 * @author warthog
 */
@Data
public class CreateWoodlandRequest implements ApprovalRequest {
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
    @Length(min = 1, max = 64, message = "样地地址长度1-32个字符")
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

    @Override
    public String toMessage() {
        return Optional.ofNullable(this.name).map(name1 -> "林地名称:" + name1).orElse("林地名称：空") + '\n' +
                Optional.ofNullable(this.country).map(country1 -> "国家:" + country1).orElse("国家：空") + '\n' +
                Optional.ofNullable(this.province).map(province1 -> "省份:" + province1).orElse("省份：空") + '\n' +
                Optional.ofNullable(this.city).map(city1 -> "城市:" + city1).orElse("城市：空") + '\n' +
                Optional.ofNullable(this.address).map(address1 -> "详细地址:" + address1).orElse("详细地址：空") + '\n' +
                Optional.ofNullable(this.position)
                        .map(position1 -> "经纬度:" + position1.getLongitude() + "," + position1.getLatitude())
                        .orElse("经纬度：空") + '\n' +
                Optional.ofNullable(this.shape).map(shape1 -> "样地形状:" + shape1.getMsg()).orElse("样地形状：空") + '\n' +
                Optional.ofNullable(this.length).map(length1 -> "长度:" + length1 + "m").orElse("长度：空") + '\n' +
                Optional.ofNullable(this.width).map(width1 -> "宽度:" + width1 + "m").orElse("宽度：空") + '\n' +
                Optional.ofNullable(this.addition).map(addition1 -> "附加信息:" + addition1).orElse("附加信息：空") + '\n';
    }
}
