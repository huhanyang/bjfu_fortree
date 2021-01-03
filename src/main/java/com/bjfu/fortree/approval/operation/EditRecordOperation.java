package com.bjfu.fortree.approval.operation;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.entity.user.User;
import com.bjfu.fortree.entity.woodland.Record;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.exception.ApprovedOperationException;
import com.bjfu.fortree.repository.woodland.RecordRepository;
import com.bjfu.fortree.request.woodland.EditRecordRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 编辑林地记录的信息
 * @author warthog
 */
@Component
public class EditRecordOperation implements ApprovedOperation {

    @Autowired
    private RecordRepository recordRepository;

    @Override
    public void execute(String applyParam, User applyUser) {
        EditRecordRequest editRecordRequest = JSONObject.parseObject(applyParam, EditRecordRequest.class);
        Optional<Record> recordOptional = recordRepository.findByIdForUpdate(editRecordRequest.getRecordId());
        if(recordOptional.isEmpty()) {
            throw new ApprovedOperationException(ResultEnum.RECORD_NOT_EXIST);
        }
        Record record = recordOptional.get();
        BeanUtils.copyProperties(editRecordRequest, record);
        recordRepository.save(record);
    }
}
