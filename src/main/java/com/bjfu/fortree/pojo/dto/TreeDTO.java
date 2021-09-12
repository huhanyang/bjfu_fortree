package com.bjfu.fortree.pojo.dto;

import com.bjfu.fortree.pojo.entity.Tree;
import com.bjfu.fortree.spatial.G2dPoint;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.Optional;


/**
 * @author warthog
 */
@Data
public class TreeDTO {

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
     * 枝下高 厘米
     */
    private Double subbranchHeight;

    /**
     * 绝对坐标
     */
    private G2dPoint absolutePosition;
    /**
     * 附加信息(JSON)
     */
    private String addition;

    public TreeDTO(Tree tree, Boolean needRecord) {
        if (tree != null) {
            BeanUtils.copyProperties(tree, this, "record");
            this.setAbsolutePosition(Optional.ofNullable(tree.getAbsolutePosition()).map(G2dPoint::new).orElse(null));
            if (needRecord) {
                this.record = new RecordDTO(tree.getRecord(), false, false, false);
            }
        }
    }
}
