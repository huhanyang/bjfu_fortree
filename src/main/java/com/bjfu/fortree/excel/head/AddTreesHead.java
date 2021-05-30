package com.bjfu.fortree.excel.head;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class AddTreesHead {
    /**
     * 树的编号
     */
    @ExcelProperty("树编号")
    private String treeId;
    /**
     * 树种
     */
    @ExcelProperty("树种")
    private String species;
    /**
     * 高度
     */
    @ExcelProperty("高度")
    private Double height;
    /**
     * 胸径
     */
    @ExcelProperty("胸径")
    private Double dbh;
    /**
     * 冠幅
     */
    @ExcelProperty("冠幅")
    private Double crownWidth;
    /**
     * 附加信息(JSON)
     */
    @ExcelProperty("附加信息")
    private String addition;
}
