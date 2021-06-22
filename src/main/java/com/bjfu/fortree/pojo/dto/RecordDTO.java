package com.bjfu.fortree.pojo.dto;

import com.bjfu.fortree.enums.entity.RecordTypeEnum;
import com.bjfu.fortree.pojo.entity.Record;
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
public class RecordDTO {

    public RecordDTO(Record record, Boolean needWoodland, Boolean needCreator, Boolean needTrees) {
        if(record != null) {
            BeanUtils.copyProperties(record, this, "woodland", "creator", "trees");
            if(needWoodland) {
                this.woodland = new WoodlandDTO(record.getWoodland(), true, false);
            }
            if(needCreator) {
                this.creator = new UserDTO(record.getCreator(), false, false, false, false);
            }
            if(needTrees) {
                this.trees = record.getTrees().stream().map(tree -> new TreeDTO(tree, false)).collect(Collectors.toList());
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
     * 所属林地
     */
    private WoodlandDTO woodland;
    /**
     * 创建人
     */
    private UserDTO creator;
    /**
     * 树木总数
     */
    private Integer treeCount;
    /**
     * 最大树高 厘米
     */
    private Double maxHeight;
    /**
     * 最小树高 厘米
     */
    private Double minHeight;
    /**
     * 平均树高 厘米
     */
    private Double meanHeight;
    /**
     * 测量时间
     */
    private Date measureTime;
    /**
     * 附加信息(JSON)
     */
    private String addition;
    /**
     * 树木是否携带编号
     */
    private Boolean isTreeWithId;
    /**
     * 类型
     */
    private RecordTypeEnum type;
    /**
     * 此记录下的单个树记录
     */
    private List<TreeDTO> trees = new ArrayList<>();
}
