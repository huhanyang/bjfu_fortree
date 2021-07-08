package com.bjfu.fortree.approval.operation;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.enums.entity.RecordTypeEnum;
import com.bjfu.fortree.pojo.entity.*;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.exception.ApprovedOperationException;
import com.bjfu.fortree.repository.woodland.RecordRepository;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import com.bjfu.fortree.pojo.request.woodland.AddRecordRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;

/**
 * 为林地添加记录
 * @author warthog
 */
@Component
public class AddRecordInWoodlandOperation implements ApprovedOperation {

    @Autowired
    private WoodlandRepository woodlandRepository;
    @Autowired
    private RecordRepository recordRepository;

    @Override
    public void execute(ApplyJob applyJob, User applyUser) {
        AddRecordRequest addRecordRequest = JSONObject.parseObject(applyJob.getApplyParam(), AddRecordRequest.class);
        Optional<Woodland> woodlandOptional = woodlandRepository.findByIdForUpdate(addRecordRequest.getWoodlandId());
        if(woodlandOptional.isEmpty()) {
            throw new ApprovedOperationException(ResultEnum.WOODLAND_NOT_EXIST);
        }
        Woodland woodland = woodlandOptional.get();
        Record record = new Record();
        BeanUtils.copyProperties(addRecordRequest, record);
        record.setWoodland(woodland);
        record.setCreator(applyUser);
        // 自动计算数据
        if(record.getType().equals(RecordTypeEnum.AUTO_CAL)) {
            record.setTreeCount(0);
            record.setMaxHeight(0.0);
            record.setMinHeight(0.0);
            record.setMeanHeight(0.0);
        }
        recordRepository.save(record);
        // 更新最新记录
        Record maxRecord = woodland.getRecords()
                .stream()
                .max(Comparator.comparing(Record::getMeasureTime))
                .filter(record1 -> record1.getMeasureTime().after(record.getMeasureTime()))
                .orElse(null);
        if(maxRecord == null) {
            woodland.setNewRecord(record);
            woodlandRepository.save(woodland);
        }
    }
}
