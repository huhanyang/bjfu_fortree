package com.bjfu.fortree.pojo.request.woodland;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

/**
 * 为林地添加记录请求
 * @author warthog
 */
@Data
public class AddRecordRequest {
    /**
     * 所属林地
     */
    @NotNull(message = "林地id不能为空")
    private Long woodlandId;
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
     * 树木是否携带编号
     */
    @NotNull(message = "树木是否携带编号不能为空")
    private Boolean isTreeWithId;
}
