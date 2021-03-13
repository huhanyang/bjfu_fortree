package com.bjfu.fortree.pojo.request.apply;

import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import lombok.Data;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author warthog
 */
@Data
public class GetAllApplyJobRequest {
    //分页
    @NotNull(message = "分页当前页数不能为空")
    private Integer current;
    @NotNull(message = "分页每页的数量不能为空")
    private Integer pageSize;
    //过滤
    private List<ApplyJobTypeEnum> type;
    private List<ApplyJobStateEnum> state;
    //排序
    private String field;
    private Sort.Direction order;
}
