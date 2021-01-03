package com.bjfu.fortree.approval.operation;

import com.bjfu.fortree.entity.user.User;
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
    public void execute(String applyParam, User applyUser) {
        Long woodlandId = Long.parseLong(applyParam);
        woodlandRepository.deleteById(woodlandId);
    }
}
