package com.bjfu.fortree.approval.operation;

import com.bjfu.fortree.entity.user.User;

/**
 * 申请审批通过后需要执行操作的接口
 * @author warthog
 */
public interface ApprovedOperation {
    /**
     * 审批通过后需要执行的操作
     * @param applyParam 申请时提交的参数
     * @param applyUser 申请人
     */
    void execute(String applyParam, User applyUser);
}
