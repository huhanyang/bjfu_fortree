package com.bjfu.fortree.approval.operation;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.exception.ApprovedOperationException;
import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.pojo.entity.User;
import com.bjfu.fortree.pojo.entity.Woodland;
import com.bjfu.fortree.pojo.request.woodland.EditWoodlandRequest;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 编辑林地信息
 *
 * @author warthog
 */
@Component
public class EditWoodlandOperation implements ApprovedOperation {

    @Autowired
    private WoodlandRepository woodlandRepository;

    @Override
    public void execute(ApplyJob applyJob, User applyUser) {
        EditWoodlandRequest editWoodlandRequest = JSONObject.parseObject(applyJob.getApplyParam(), EditWoodlandRequest.class);
        Optional<Woodland> woodlandOptional = woodlandRepository.findByIdForUpdate(editWoodlandRequest.getWoodlandId());
        if (woodlandOptional.isEmpty()) {
            throw new ApprovedOperationException(ResultEnum.WOODLAND_NOT_EXIST);
        }
        Woodland woodland = woodlandOptional.get();
        BeanUtils.copyProperties(editWoodlandRequest, woodland);
        woodland.setPosition(editWoodlandRequest.getPosition().convertToGeom());
        woodlandRepository.save(woodland);
    }
}
