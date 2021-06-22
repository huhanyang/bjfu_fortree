package com.bjfu.fortree.pojo.vo;

import com.bjfu.fortree.enums.entity.RecordTypeEnum;
import com.bjfu.fortree.pojo.dto.RecordDTO;
import com.bjfu.fortree.pojo.dto.TreeDTO;
import com.bjfu.fortree.pojo.dto.UserDTO;
import com.bjfu.fortree.pojo.dto.WoodlandDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class RecordVO {

    public RecordVO(RecordDTO recordDTO) {
        if(recordDTO != null) {
            BeanUtils.copyProperties(recordDTO, this);
            this.woodland = Optional.ofNullable(recordDTO.getWoodland()).map(WoodlandVO::new).orElse(null);
            this.creator = Optional.ofNullable(recordDTO.getCreator()).map(UserVO::new).orElse(null);
            this.trees = Optional.ofNullable(recordDTO.getTrees())
                    .map(treeDTOS -> treeDTOS.stream().map(TreeVO::new).collect(Collectors.toList()))
                    .orElse(null);
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
    private WoodlandVO woodland;
    /**
     * 创建人
     */
    private UserVO creator;
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
    private List<TreeVO> trees = new ArrayList<>();
}
