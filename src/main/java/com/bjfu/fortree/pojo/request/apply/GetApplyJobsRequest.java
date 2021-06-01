package com.bjfu.fortree.pojo.request.apply;

import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import com.bjfu.fortree.pojo.request.BasePageAndSorterRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author warthog
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetApplyJobsRequest extends BasePageAndSorterRequest {
    /**
     * 按照类型匹配
     */
    private List<ApplyJobTypeEnum> type;
    /**
     * 按照状态匹配
     */
    private List<ApplyJobStateEnum> state;
}
