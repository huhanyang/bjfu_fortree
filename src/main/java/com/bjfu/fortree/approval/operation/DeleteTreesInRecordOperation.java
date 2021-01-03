package com.bjfu.fortree.approval.operation;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.entity.user.User;
import com.bjfu.fortree.repository.woodland.TreeRepository;
import com.bjfu.fortree.request.woodland.DeleteTreesRequest;
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
    public void execute(String applyParam, User applyUser) {
        DeleteTreesRequest deleteTreesRequest = JSONObject.parseObject(applyParam, DeleteTreesRequest.class);
        treeRepository.deleteByIdIn(deleteTreesRequest.getTreeIds());
    }
}
