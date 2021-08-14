package com.bjfu.fortree.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperationDispatch;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import com.bjfu.fortree.excel.head.RecordInfoHead;
import com.bjfu.fortree.excel.head.TreeInfoHead;
import com.bjfu.fortree.excel.head.WoodlandInfoHead;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.exception.WrongParamException;
import com.bjfu.fortree.pojo.dto.ApplyJobDTO;
import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.pojo.entity.User;
import com.bjfu.fortree.pojo.entity.Woodland;
import com.bjfu.fortree.pojo.request.export.ExportWoodlandsInBoundsRequest;
import com.bjfu.fortree.repository.job.ApplyJobRepository;
import com.bjfu.fortree.repository.user.AuthorityRepository;
import com.bjfu.fortree.repository.user.UserRepository;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import com.bjfu.fortree.service.ExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author warthog
 */
@Slf4j
@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private WoodlandRepository woodlandRepository;
    @Autowired
    private ApplyJobRepository applyJobRepository;
    @Autowired
    private ApprovedOperationDispatch approvedOperationDispatch;

    @Override
    @Transactional
    public void exportWoodlandDetailInfo(Long woodlandId, HttpServletResponse response) {
        // 获取林地信息
        Woodland woodland = woodlandRepository.findById(woodlandId)
                .orElseThrow(() -> new WrongParamException(ResultEnum.WOODLAND_NOT_EXIST));
        // 生成文件名
        String fileName = URLEncoder.encode(woodland.getName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20") + ".xlsx";
        // 设置http响应头
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        try {
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).autoCloseStream(false).build();
            // 写入林地信息sheet
            WriteSheet woodlandSheet = EasyExcel.writerSheet("林地信息").build();
            WriteTable woodlandInfoTable = EasyExcel.writerTable(0).needHead(Boolean.TRUE).head(WoodlandInfoHead.class).build();
            WriteTable recordsInfoTable = EasyExcel.writerTable(1).needHead(Boolean.TRUE).head(RecordInfoHead.class).build();
            excelWriter.write(Collections.singletonList(new WoodlandInfoHead(woodland)), woodlandSheet, woodlandInfoTable);
            excelWriter.write(woodland.getRecords().stream().map(RecordInfoHead::new).collect(Collectors.toList()),
                    woodlandSheet, recordsInfoTable);
            // 写入多个记录信息sheet
            woodland.getRecords().forEach(record -> {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                WriteSheet recordSheet = EasyExcel.writerSheet("记录" + simpleDateFormat.format(record.getCreatedTime())).build();
                WriteTable recordInfoTable = EasyExcel.writerTable(0).needHead(Boolean.TRUE).head(RecordInfoHead.class).build();
                WriteTable treesInfoTable = EasyExcel.writerTable(1).needHead(Boolean.TRUE).head(TreeInfoHead.class).build();
                excelWriter.write(Collections.singletonList(new RecordInfoHead(record)), recordSheet, recordInfoTable);
                excelWriter.write(record.getTrees().stream().map(TreeInfoHead::new).collect(Collectors.toList()),
                        recordSheet, treesInfoTable);
            });
            // 输出文件
            excelWriter.finish();
        } catch (Exception e) {
            log.error("导出错误", e);
            throw new SystemWrongException(ResultEnum.FILE_EXPORT_FAILED);
        }
    }

    @Override
    @Transactional
    public ApplyJobDTO exportWoodlandsInfo(String userAccount, List<Long> woodlandIds) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 生成申请信息简介
        StringBuilder stringBuilder = new StringBuilder();
        woodlandRepository.findAllById(woodlandIds)
                .forEach(woodland -> stringBuilder.append(woodland.getName()).append(','));
        String exportWoodlandsName = stringBuilder.toString();
        if (stringBuilder.capacity() > 250) {
            exportWoodlandsName = stringBuilder.substring(0, 250) + "...";
        }
        // 序列化申请参数
        String applyParam = JSONObject.toJSONString(woodlandIds);
        // 判断是否拥有免审批权限
        if (authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.EXPORT_ANY_INFO)) {
            // 生成状态为通过的申请实体
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.EXPORT_WOODLANDS_INFO, applyParam,
                    exportWoodlandsName);
            // 申请实体落库
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作
            approvedOperationDispatch.asyncDispatch(passedApply);
            return new ApplyJobDTO(passedApply, false, false, false, false);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.EXPORT_WOODLANDS_INFO, applyParam,
                    exportWoodlandsName);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply, false, false, false, false);
        }
    }

    @Override
    @Transactional
    public ApplyJobDTO exportWoodlandsInfoInBounds(String userAccount, ExportWoodlandsInBoundsRequest request) {
        // 查找用户
        User user = userRepository.findByAccount(userAccount)
                .orElseThrow(() -> new SystemWrongException(ResultEnum.JWT_USER_INFO_ERROR));
        // 生成申请信息简介
        StringBuilder stringBuilder = new StringBuilder();
        woodlandRepository.findAllInBounds(request.getPolygon().convertToGeom())
                .forEach(woodland -> stringBuilder.append(woodland.getName()).append(','));
        String exportWoodlandsName = stringBuilder.toString();
        if (stringBuilder.capacity() > 250) {
            exportWoodlandsName = stringBuilder.substring(0, 250) + "...";
        }
        // 序列化申请参数
        String applyParam = JSONObject.toJSONString(request);
        // 判断是否拥有免审批权限
        if (authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.EXPORT_ANY_INFO)) {
            // 生成状态为通过的申请实体
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.EXPORT_WOODLANDS_INFO, applyParam,
                    exportWoodlandsName);
            // 申请实体落库
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作
            approvedOperationDispatch.asyncDispatch(passedApply);
            return new ApplyJobDTO(passedApply, false, false, false, false);
        } else {
            // 生成状态为申请中的申请实体
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.EXPORT_WOODLANDS_INFO, applyParam,
                    exportWoodlandsName);
            // 申请实体落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply, false, false, false, false);
        }
    }

}
