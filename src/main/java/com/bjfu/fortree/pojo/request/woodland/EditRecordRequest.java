package com.bjfu.fortree.pojo.request.woodland;

import com.bjfu.fortree.enums.entity.RecordTypeEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

/**
 * 编辑林地记录请求
 * @author warthog
 */
@Data
public class EditRecordRequest {
    /**
     * 林地记录的id
     */
    @NotNull(message = "林地记录的id不能为空")
    Long recordId;
    /**
     * 树木总数
     */
    @NotNull(message = "树木总数不能为空")
    private Integer treeCount;
    /**
     * 最大树高
     */
    @NotNull(message = "最大树高不能为空")
    private Double maxHeight;
    /**
     * 最小树高
     */
    @NotNull(message = "最小树高不能为空")
    private Double minHeight;
    /**
     * 平均树高
     */
    @NotNull(message = "平均树高不能为空")
    private Double meanHeight;
    /**
     * 测量时间
     */
    @NotNull(message = "测量时间不能为空")
    @Past(message = "测量时间必须是过去的时间")
    private Date measureTime;
    /**
     * 附加信息(JSON)
     */
    @Length(max = 512, message = "附加信息长度最大512个字符")
    private String addition;
    /**
     * 记录类型
     */
    @NotNull(message = "及记录类型不能为空")
    private RecordTypeEnum type;
}
