package com.bjfu.fortree.pojo.vo;

import com.bjfu.fortree.pojo.dto.TreeDTO;
import com.bjfu.fortree.spatial.G2dPoint;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.Optional;

@Data
public class TreeVO {

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
    private RecordVO record;
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

    public TreeVO(TreeDTO treeDTO) {
        if (treeDTO != null) {
            BeanUtils.copyProperties(treeDTO, this);
            this.record = Optional.ofNullable(treeDTO.getRecord()).map(RecordVO::new).orElse(null);
        }
    }

}
