package com.bjfu.fortree.excel.head;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bjfu.fortree.pojo.entity.Record;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author warthog
 */
@Data
public class RecordInfoHead {
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
     * 树木总数
     */
    @ExcelProperty("树木总数")
    private Integer treeCount;
    /**
     * 最大树高 厘米
     */
    @ExcelProperty("最大树高(cm)")
    private Double maxHeight;
    /**
     * 最小树高 厘米
     */
    @ExcelProperty("最小树高(cm)")
    private Double minHeight;
    /**
     * 平均树高 厘米
     */
    @ExcelProperty("平均树高(cm)")
    private Double meanHeight;
    /**
     * 测量时间
     */
    @ExcelProperty("测量时间")
    private Date measureTime;
    /**
     * 附加信息(JSON)
     */
    @ExcelProperty("附加信息")
    private String addition;
    public RecordInfoHead() {
    }
    public RecordInfoHead(Record record) {
        BeanUtils.copyProperties(record, this);
        this.creatorAccount = record.getCreator().getAccount();
        this.creatorName = record.getCreator().getName();
    }
}
