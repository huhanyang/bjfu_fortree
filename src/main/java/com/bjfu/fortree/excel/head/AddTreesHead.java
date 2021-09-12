package com.bjfu.fortree.excel.head;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class AddTreesHead {

    /**
     * 树木编号
     */
    @ExcelProperty("树木编号")
    private String treeId;
    /**
     * 树种
     */
    @ExcelProperty("树种")
    private String species;
    /**
     * 高度 厘米
     */
    @ExcelProperty("高度(cm)")
    private Double height;
    /**
     * 胸径 厘米
     */
    @ExcelProperty("胸径(cm)")
    private Double dbh;
    /**
     * 冠幅 厘米
     */
    @ExcelProperty("冠幅(cm)")
    private Double crownWidth;
    /**
     * 枝下高 厘米
     */
    @ExcelProperty("枝下高(cm)")
    private Double subbranchHeight;

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
     * 附加信息(JSON)
     */
    @ExcelProperty("附加信息")
    private String addition;
}
