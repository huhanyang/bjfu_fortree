package com.bjfu.fortree.excel.head;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bjfu.fortree.pojo.entity.woodland.Woodland;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author warthog
 */
@Data
public class WoodlandInfoHead {
    public WoodlandInfoHead() {}
    public WoodlandInfoHead(Woodland woodland) {
        BeanUtils.copyProperties(woodland, this);
        this.creatorAccount = woodland.getCreator().getAccount();
        this.creatorName = woodland.getCreator().getName();
        this.longitude = woodland.getPosition().getPosition().getLon();
        this.latitude = woodland.getPosition().getPosition().getLat();
        this.shape = woodland.getShape().getMsg();
        woodland.getRecords().stream().findFirst()
                .ifPresent(record -> {
                    BeanUtils.copyProperties(record, this,
                            "addition", "creatorAccount", "creatorName");
                });
    }
    /**
     * 样地名称
     */
    @ExcelProperty("林地名称")
    private String name;
    /**
     * 用户名
     */
    @ExcelProperty({"创建人", "账号"})
    private String creatorAccount;
    /**
     * 姓名
     */
    @ExcelProperty({"创建人", "姓名"})
    private String creatorName;
    /**
     * 经度
     */
    @ExcelProperty({"坐标", "经度"})
    private double longitude;
    /**
     * 纬度
     */
    @ExcelProperty({"坐标", "纬度"})
    private double latitude;
    /**
     * 国家
     */
    @ExcelProperty({"林地位置", "国家"})
    private String country;
    /**
     * 省/州
     */
    @ExcelProperty({"林地位置", "省/州"})
    private String province;
    /**
     * 城市
     */
    @ExcelProperty({"林地位置", "城市"})
    private String city;
    /**
     * 样地地址
     */
    @ExcelProperty("详细地址")
    private String address;
    /**
     * 形状
     */
    @ExcelProperty("形状")
    private String shape;
    /**
     * 长度 米
     */
    @ExcelProperty("长度(m)")
    private Double length;
    /**
     * 宽度 米
     */
    @ExcelProperty("宽度(m)")
    private Double width;
    /**
     * 附加信息(JSON)
     */
    @ExcelProperty("附加信息")
    private String addition;

    /**
     * 树木总数
     */
    @ExcelProperty({"最新记录", "树木总数"})
    private Integer treeCount;
    /**
     * 最大树高 厘米
     */
    @ExcelProperty({"最新记录", "最大树高(cm)"})
    private Double maxHeight;
    /**
     * 最小树高 厘米
     */
    @ExcelProperty({"最新记录", "最小树高(cm)"})
    private Double minHeight;
    /**
     * 平均树高 厘米
     */
    @ExcelProperty({"最新记录", "平均树高(cm)"})
    private Double meanHeight;
    /**
     * 测量时间
     */
    @ExcelProperty({"最新记录", "测量时间"})
    private Date measureTime;
}
