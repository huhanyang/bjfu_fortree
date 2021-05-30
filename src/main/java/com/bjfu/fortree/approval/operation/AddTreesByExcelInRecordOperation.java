package com.bjfu.fortree.approval.operation;

import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.config.MinioConfig;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.excel.parser.AddTreesExcelParser;
import com.bjfu.fortree.exception.ApprovedOperationException;
import com.bjfu.fortree.pojo.entity.apply.ApplyJob;
import com.bjfu.fortree.pojo.entity.user.User;
import com.bjfu.fortree.pojo.entity.woodland.Record;
import com.bjfu.fortree.pojo.entity.woodland.Tree;
import com.bjfu.fortree.pojo.request.woodland.AddTreesRequest;
import com.bjfu.fortree.repository.woodland.RecordRepository;
import com.bjfu.fortree.repository.woodland.TreeRepository;
import com.bjfu.fortree.service.OssService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 通过excel为林地的记录添加树木
 * @author warthog
 */
@Component
public class AddTreesByExcelInRecordOperation implements ApprovedOperation {


    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private TreeRepository treeRepository;
    @Autowired
    private OssService ossService;

    @Override
    public void execute(ApplyJob applyJob, User applyUser) {
        Long recordId = Long.parseLong(applyJob.getApplyParam());
        Optional<Record> recordOptional = recordRepository.findByIdForUpdate(recordId);
        if(recordOptional.isEmpty()) {
            throw new ApprovedOperationException(ResultEnum.RECORD_NOT_EXIST);
        }
        Record record = recordOptional.get();
        // 获取上传的文件
        InputStream inputStream = ossService.getObject(applyJob.getDownloadFile().getOssBucketName(), applyJob.getDownloadFile().getOssObjectName());
        // 解析出tree参数
        List<AddTreesRequest.Tree> trees = AddTreesExcelParser.parse(inputStream);
        // 保存树木
        List<Tree> addTress = trees.stream().map(tree -> {
            Tree treeEntity = new Tree();
            BeanUtils.copyProperties(tree, treeEntity);
            treeEntity.setRecord(record);
            return treeEntity;
        }).collect(Collectors.toList());
        treeRepository.saveAll(addTress);
    }


}
