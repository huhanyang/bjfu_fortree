package com.bjfu.fortree.approval;

import com.bjfu.fortree.pojo.entity.apply.ApplyJob;
import com.bjfu.fortree.pojo.entity.user.User;

/**
 * 申请审批通过后需要执行操作的接口
 * @author warthog
 */
public interface ApprovedOperation {
    /**
     * 审批通过后需要执行的操作
     * @param applyJob 申请实体
     * @param applyUser 申请人
     */
    void execute(ApplyJob applyJob, User applyUser);
}
