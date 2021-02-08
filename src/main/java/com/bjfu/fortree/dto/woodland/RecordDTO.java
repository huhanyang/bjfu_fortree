package com.bjfu.fortree.dto.woodland;

import com.bjfu.fortree.dto.user.UserDTO;
import com.bjfu.fortree.entity.woodland.Record;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author warthog
 */
@Data
public class RecordDTO {
    public RecordDTO(Record record) {
        BeanUtils.copyProperties(record, this, "creator");
        this.setCreator(new UserDTO(record.getCreator()));
    }
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
}
