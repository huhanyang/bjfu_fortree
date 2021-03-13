package com.bjfu.fortree.approval.operation;

import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.pojo.entity.apply.ApplyJob;
import com.bjfu.fortree.pojo.entity.user.User;
import com.bjfu.fortree.repository.woodland.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 删除林地中的记录
 * @author warthog
 */
@Component
public class DeleteRecordInWoodlandOperation implements ApprovedOperation {

    @Autowired
    private RecordRepository recordRepository;

    @Override
    public void execute(ApplyJob applyJob, User applyUser) {
        Long recordId = Long.parseLong(applyJob.getApplyParam());
        recordRepository.deleteById(recordId);
    }
}
