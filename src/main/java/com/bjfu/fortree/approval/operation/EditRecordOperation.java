package com.bjfu.fortree.approval.operation;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.RecordTypeEnum;
import com.bjfu.fortree.exception.ApprovedOperationException;
import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.pojo.entity.Record;
import com.bjfu.fortree.pojo.entity.Tree;
import com.bjfu.fortree.pojo.entity.User;
import com.bjfu.fortree.pojo.request.woodland.EditRecordRequest;
import com.bjfu.fortree.repository.woodland.RecordRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 编辑林地记录的信息
 *
 * @author warthog
 */
@Component
public class EditRecordOperation implements ApprovedOperation {

    @Autowired
    private RecordRepository recordRepository;

    @Override
    public void execute(ApplyJob applyJob, User applyUser) {
        EditRecordRequest editRecordRequest = JSONObject.parseObject(applyJob.getApplyParam(), EditRecordRequest.class);
        Optional<Record> recordOptional = recordRepository.findByIdForUpdate(editRecordRequest.getRecordId());
        if (recordOptional.isEmpty()) {
            throw new ApprovedOperationException(ResultEnum.RECORD_NOT_EXIST);
        }
        Record record = recordOptional.get();
        BeanUtils.copyProperties(editRecordRequest, record);
        if (record.getType().equals(RecordTypeEnum.AUTO_CAL)) {
            int treeCount = 0;
            double maxHeight = 0;
            double minHeight = Integer.MAX_VALUE;
            double heightCount = 0;
            for (Tree tree : record.getTrees()) {
                treeCount += 1;
                if (maxHeight < tree.getHeight()) {
                    maxHeight = tree.getHeight();
                }
                if (minHeight > tree.getHeight()) {
                    minHeight = tree.getHeight();
                }
                heightCount += tree.getHeight();
            }
            record.setTreeCount(treeCount);
            record.setMaxHeight(maxHeight);
            record.setMinHeight(minHeight);
            record.setMeanHeight(treeCount == 0 ? 0 : heightCount / treeCount);
        }
        recordRepository.save(record);
    }
}
