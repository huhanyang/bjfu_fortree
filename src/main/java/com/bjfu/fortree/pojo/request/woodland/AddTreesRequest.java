package com.bjfu.fortree.pojo.request.woodland;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 为记录添加树木的请求
 * @author warthog
 */
@Data
public class AddTreesRequest {

    /**
     * 所属记录
     */
    @NotNull(message = "记录的id不能为空")
    private Long recordId;

    /**
     * 要添加的树木列表
     */
    @NotEmpty(message = "添加的树木列表不能为空")
    private List<@NotNull @Valid Tree> trees;

    /**
     * 树木内部类
     */
    @Data
    public static class Tree{
        /**
         * 树的编号
         */
        @Length(max = 32, message = "树的编号长度最大32个字符")
        private String treeId;
        /**
         * 树种
         */
        @NotNull(message = "树种不能为空")
        @Length(min = 1, max = 32, message = "树种长度为1-32个字符")
        private String species;
        /**
         * 高度
         */
        @NotNull(message = "树高不能为空")
        private Double height;
        /**
         * 胸径
         */
        @NotNull(message = "胸径不能为空")
        private Double dbh;
        /**
         * 冠幅
         */
        @NotNull(message = "冠幅不能为空")
        private Double crownWidth;
        /**
         * 附加信息(JSON)
         */
        @Length(max = 512, message = "附加信息长度最大512个字符")
        private String addition;
    }
}
