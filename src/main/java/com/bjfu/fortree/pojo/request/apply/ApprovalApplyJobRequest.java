package com.bjfu.fortree.pojo.request.apply;

import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 审批申请的请求
 *
 * @author warthog
 */
@Data
public class ApprovalApplyJobRequest {

    @NotNull(message = "申请id不能为空")
    private Long applyJobId;
    @NotNull(message = "变更后的状态不能为空")
    private ApplyJobStateEnum state;
    @NotBlank(message = "信息不能为空!")
    @Length(min = 1, max = 64, message = "信息长度在1-64位!")
    private String msg;

}
