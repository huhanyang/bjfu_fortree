package com.bjfu.fortree.approval.operation;

import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.RecordTypeEnum;
import com.bjfu.fortree.excel.parser.AddTreesExcelParser;
import com.bjfu.fortree.exception.ApprovedOperationException;
import com.bjfu.fortree.pojo.entity.*;
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
 *
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
        if (recordOptional.isEmpty()) {
            throw new ApprovedOperationException(ResultEnum.RECORD_NOT_EXIST);
        }
        Record record = recordOptional.get();
        // 获取上传的文件
        OssFile uploadFile = applyJob.getUploadFile();
        InputStream inputStream = ossService.getObject(uploadFile.getOssBucketName(), uploadFile.getOssObjectName());
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
        if (record.getType().equals(RecordTypeEnum.AUTO_CAL)) {
            int treeCount = 0;
            double maxHeight = 0;
            double minHeight = Integer.MAX_VALUE;
            double heightCount = 0;
            for (Tree tree : record.getTrees()) {
                treeCount += 1;
                if (maxHeight < tree.getHeight()) {
                    maxHeight = tree.getHeight();
                }
                if (minHeight > tree.getHeight()) {
                    minHeight = tree.getHeight();
                }
                heightCount += tree.getHeight();
            }
            record.setTreeCount(treeCount);
            record.setMaxHeight(maxHeight);
            record.setMinHeight(minHeight);
            record.setMeanHeight(treeCount == 0 ? 0 : heightCount / treeCount);
            recordRepository.save(record);
        }
    }


}
