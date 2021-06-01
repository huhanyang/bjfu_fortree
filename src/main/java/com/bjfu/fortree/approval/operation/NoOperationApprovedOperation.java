package com.bjfu.fortree.approval.operation;

import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.pojo.entity.User;

/**
 * 申请通过不许任何操作的后置执行类
 * @author warthog
 */
public class NoOperationApprovedOperation implements ApprovedOperation {
    @Override
    public void execute(ApplyJob applyJob, User applyUser) {
    }
}
