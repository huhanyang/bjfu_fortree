package com.bjfu.fortree.pojo.request.woodland;

import com.bjfu.fortree.enums.entity.RecordTypeEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

/**
 * 为林地添加记录请求
 *
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
    /**
     * 记录类型
     */
    @NotNull(message = "记录类型不能为空")
    private RecordTypeEnum type;
    /**
     * 树高测量方式
     */
    @NotNull(message = "树高测量方式不能为空")
    private String measureType;
    /**
     * 郁闭度 0-100(%)
     */
    @NotNull(message = "郁闭度不能为空")
    private Double canopyDensity;
    /**
     * 优势树种
     */
    @NotNull(message = "优势树种不能为空")
    private String dominantSpecies;

    /**
     * 龄组
     */
    private String ageGroup;
    /**
     * 坡度
     */
    private String slope;
    /**
     * 坡向
     */
    private String aspect;
    /**
     * 起源
     */
    private String origin;
    /**
     * 树种组成
     */
    private String speciesComposition;
}
