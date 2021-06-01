package com.bjfu.fortree.pojo.dto;

import com.bjfu.fortree.pojo.entity.Tree;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;


/**
 * @author warthog
 */
@Data
public class TreeDTO {

    public TreeDTO(Tree tree, Boolean needRecord) {
        if(tree != null) {
            BeanUtils.copyProperties(tree, this, "record");
            if(needRecord) {
                this.record = new RecordDTO(tree.getRecord(), false, false, false);
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
     * 所属记录
     */
    private RecordDTO record;
    /**
     * 树的编号
     */
    private String treeId;
    /**
     * 树种
     */
    private String species;
    /**
     * 高度 厘米
     */
    private Double height;
    /**
     * 胸径 厘米
     */
    private Double dbh;
    /**
     * 冠幅 厘米
     */
    private Double crownWidth;
    /**
     * 附加信息(JSON)
     */
    private String addition;
}
