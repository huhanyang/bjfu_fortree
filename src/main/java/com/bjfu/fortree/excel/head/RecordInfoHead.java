package com.bjfu.fortree.excel.head;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bjfu.fortree.pojo.dto.RecordDTO;
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
     * 树高测量方式
     */
    @ExcelProperty("树高测量方式")
    private String measureType;
    /**
     * 郁闭度 0-100(%)
     */
    @ExcelProperty("郁闭度 0-100(%)")
    private Double canopyDensity;
    /**
     * 优势树种
     */
    @ExcelProperty("优势树种")
    private String dominantSpecies;

    /**
     * 龄组
     */
    @ExcelProperty("龄组")
    private String ageGroup;
    /**
     * 坡度
     */
    @ExcelProperty("坡度")
    private String slope;
    /**
     * 坡向
     */
    @ExcelProperty("坡向")
    private String aspect;
    /**
     * 起源
     */
    @ExcelProperty("起源")
    private String origin;
    /**
     * 树种组成
     */
    @ExcelProperty("树种组成")
    private String speciesComposition;
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
        RecordDTO recordDTO = new RecordDTO(record, false, true, false);
        BeanUtils.copyProperties(recordDTO, this);
        this.creatorAccount = recordDTO.getCreator().getAccount();
        this.creatorName = recordDTO.getCreator().getName();
    }
}
