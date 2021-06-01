package com.bjfu.fortree.approval.operation;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.pojo.entity.User;
import com.bjfu.fortree.repository.woodland.TreeRepository;
import com.bjfu.fortree.pojo.request.woodland.DeleteTreesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 删除林地记录中的一部分树木
 * @author warthog
 */
@Component
public class DeleteTreesInRecordOperation implements ApprovedOperation {

    @Autowired
    private TreeRepository treeRepository;

    @Override
    public void execute(ApplyJob applyJob, User applyUser) {
        DeleteTreesRequest deleteTreesRequest = JSONObject.parseObject(applyJob.getApplyParam(), DeleteTreesRequest.class);
        treeRepository.deleteByIdIn(deleteTreesRequest.getTreeIds());
    }
}
