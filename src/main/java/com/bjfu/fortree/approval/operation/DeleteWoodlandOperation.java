package com.bjfu.fortree.approval.operation;

import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.pojo.entity.apply.ApplyJob;
import com.bjfu.fortree.pojo.entity.user.User;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 删除林地
 * @author warthog
 */
@Component
public class DeleteWoodlandOperation implements ApprovedOperation {

    @Autowired
    WoodlandRepository woodlandRepository;

    @Override
    public void execute(ApplyJob applyJob, User applyUser) {
        Long woodlandId = Long.parseLong(applyJob.getApplyParam());
        woodlandRepository.deleteById(woodlandId);
    }
}
