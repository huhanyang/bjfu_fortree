package com.bjfu.fortree.approval.operation;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.entity.user.User;
import com.bjfu.fortree.entity.woodland.Woodland;
import com.bjfu.fortree.repository.user.UserRepository;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import com.bjfu.fortree.request.woodland.CreateWoodlandRequest;
import com.bjfu.fortree.spatial.G2DPoint;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建林地申请通过的后置执行类
 * @author warthog
 */
@Component
public class CreateWoodlandApprovedOperation implements ApprovedOperation{

    @Autowired
    private WoodlandRepository woodlandRepository;

    @Override
    public void execute(String applyParam, User applyUser) {
        CreateWoodlandRequest createWoodlandRequest = JSONObject.parseObject(applyParam, CreateWoodlandRequest.class);
        Woodland woodland = new Woodland();
        BeanUtils.copyProperties(createWoodlandRequest, woodland);
        woodland.setCreator(applyUser);
        woodland.setPosition(G2DPoint.convertToGeom(createWoodlandRequest.getPosition()));
        woodlandRepository.save(woodland);
    }
}
