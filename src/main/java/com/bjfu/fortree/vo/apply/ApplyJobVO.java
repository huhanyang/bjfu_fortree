package com.bjfu.fortree.vo.apply;

import com.bjfu.fortree.dto.job.ApplyJobDTO;
import com.bjfu.fortree.dto.user.UserDTO;
import com.bjfu.fortree.enums.entity.ApplyJobStateEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import com.bjfu.fortree.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author warthog
 */
@Data
public class ApplyJobVO {
    public ApplyJobVO() {}
    public ApplyJobVO(ApplyJobDTO applyJobDTO) {
        BeanUtils.copyProperties(applyJobDTO, this);
        this.applyUser = new UserVO(applyJobDTO.getApplyUser());
        this.operateUser = new UserVO(applyJobDTO.getOperateUser());
    }
    /**
     * 申请类型
     */
    private ApplyJobTypeEnum type;
    /**
     * 申请参数
     */
    private String applyParam;
    /**
     * 申请状态
     */
    private ApplyJobStateEnum state;
    /**
     * 申请人
     */
    private UserVO applyUser;
    /**
     * 状态操作人
     */
    private UserVO operateUser;
    /**
     * 申请通过/未通过的时间
     */
    private Date operateTime;
}
