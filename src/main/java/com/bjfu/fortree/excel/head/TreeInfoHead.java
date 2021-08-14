package com.bjfu.fortree.excel.head;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bjfu.fortree.pojo.entity.Tree;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author warthog
 */
@Data
public class TreeInfoHead {
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
     * 附加信息(JSON)
     */
    @ExcelProperty("附加信息")
    private String addition;
    public TreeInfoHead() {
    }
    public TreeInfoHead(Tree tree) {
        BeanUtils.copyProperties(tree, this);
    }
}
