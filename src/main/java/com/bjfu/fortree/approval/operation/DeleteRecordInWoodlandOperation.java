package com.bjfu.fortree.approval.operation;

import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.pojo.entity.Record;
import com.bjfu.fortree.pojo.entity.User;
import com.bjfu.fortree.pojo.entity.Woodland;
import com.bjfu.fortree.repository.woodland.RecordRepository;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * 删除林地中的记录
 * @author warthog
 */
@Component
public class DeleteRecordInWoodlandOperation implements ApprovedOperation {

    @Autowired
    private WoodlandRepository woodlandRepository;
    @Autowired
    private RecordRepository recordRepository;

    @Override
    public void execute(ApplyJob applyJob, User applyUser) {
        Long recordId = Long.parseLong(applyJob.getApplyParam());
        Record record = recordRepository.findById(recordId).orElse(null);
        if(record!=null) {
            Woodland woodland = record.getWoodland();
            // 更新最新记录
            Record newRecord = woodland.getRecords()
                    .stream()
                    .filter(record1 -> !record1.getId().equals(record.getId()))
                    .max(Comparator.comparing(Record::getMeasureTime))
                    .orElse(null);
            woodland.setNewRecord(newRecord);
            woodlandRepository.save(woodland);
            recordRepository.delete(record);
        }
    }
}
