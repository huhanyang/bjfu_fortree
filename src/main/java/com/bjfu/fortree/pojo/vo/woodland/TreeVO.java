package com.bjfu.fortree.pojo.vo.woodland;

import com.bjfu.fortree.pojo.dto.woodland.TreeDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author warthog
 */
@Data
public class TreeVO {
    public TreeVO(TreeDTO treeDTO) {
        BeanUtils.copyProperties(treeDTO, this);
    }
    /**
     * 树木数据库id
     */
    private Long id;
    /**
     * 树木id
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
