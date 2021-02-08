package com.bjfu.fortree.vo.woodland;

import com.bjfu.fortree.dto.woodland.RecordDTO;
import com.bjfu.fortree.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author warthog
 */
@Data
public class RecordVO {
    public RecordVO(RecordDTO recordDTO) {
        BeanUtils.copyProperties(recordDTO, this, "creator");
        this.setCreator(new UserVO(recordDTO.getCreator()));
    }
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
}
