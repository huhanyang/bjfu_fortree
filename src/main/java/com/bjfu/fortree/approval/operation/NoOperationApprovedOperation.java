package com.bjfu.fortree.approval.operation;

import com.bjfu.fortree.entity.user.User;

/**
 * 申请通过不许任何操作的后置执行类
 * @author warthog
 */
public class NoOperationApprovedOperation implements ApprovedOperation{
    @Override
    public void execute(String applyParam, User applyUser) {
    }
}
