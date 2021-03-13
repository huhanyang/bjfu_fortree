package com.bjfu.fortree.approval.operation;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.pojo.entity.apply.ApplyJob;
import com.bjfu.fortree.pojo.entity.user.User;
import com.bjfu.fortree.pojo.entity.woodland.Record;
import com.bjfu.fortree.pojo.entity.woodland.Tree;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.exception.ApprovedOperationException;
import com.bjfu.fortree.repository.woodland.RecordRepository;
import com.bjfu.fortree.repository.woodland.TreeRepository;
import com.bjfu.fortree.pojo.request.woodland.AddTreesRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 为林地的记录添加树木
 * @author warthog
 */
@Component
public class AddTreesInRecordOperation implements ApprovedOperation {

    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private TreeRepository treeRepository;

    @Override
    public void execute(ApplyJob applyJob, User applyUser) {
        AddTreesRequest addTreesRequest = JSONObject.parseObject(applyJob.getApplyParam(), AddTreesRequest.class);
        Optional<Record> recordOptional = recordRepository.findByIdForUpdate(addTreesRequest.getRecordId());
        if(recordOptional.isEmpty()) {
            throw new ApprovedOperationException(ResultEnum.RECORD_NOT_EXIST);
        }
        Record record = recordOptional.get();
        List<Tree> addTress = addTreesRequest.getTrees().stream().map(tree -> {
            Tree treeEntity = new Tree();
            BeanUtils.copyProperties(tree, treeEntity);
            treeEntity.setRecord(record);
            return treeEntity;
        }).collect(Collectors.toList());
        treeRepository.saveAll(addTress);
    }
}
