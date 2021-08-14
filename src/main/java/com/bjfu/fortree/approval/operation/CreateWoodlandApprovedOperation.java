package com.bjfu.fortree.approval.operation;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.pojo.entity.User;
import com.bjfu.fortree.pojo.entity.Woodland;
import com.bjfu.fortree.pojo.request.woodland.CreateWoodlandRequest;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import com.bjfu.fortree.spatial.G2dPoint;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建林地申请通过的后置执行类
 *
 * @author warthog
 */
@Component
public class CreateWoodlandApprovedOperation implements ApprovedOperation {

    @Autowired
    private WoodlandRepository woodlandRepository;

    @Override
    public void execute(ApplyJob applyJob, User applyUser) {
        CreateWoodlandRequest createWoodlandRequest = JSONObject.parseObject(applyJob.getApplyParam(), CreateWoodlandRequest.class);
        Woodland woodland = new Woodland();
        BeanUtils.copyProperties(createWoodlandRequest, woodland);
        woodland.setCreator(applyUser);
        woodland.setPosition(G2dPoint.convertToGeom(createWoodlandRequest.getPosition()));
        woodlandRepository.save(woodland);
    }
}
