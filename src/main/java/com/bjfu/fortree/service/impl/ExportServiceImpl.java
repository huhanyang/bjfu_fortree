package com.bjfu.fortree.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperationDispatch;
import com.bjfu.fortree.pojo.dto.job.ApplyJobDTO;
import com.bjfu.fortree.pojo.entity.apply.ApplyJob;
import com.bjfu.fortree.pojo.entity.user.User;
import com.bjfu.fortree.pojo.entity.woodland.Woodland;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.enums.entity.ApplyJobTypeEnum;
import com.bjfu.fortree.enums.entity.AuthorityTypeEnum;
import com.bjfu.fortree.excel.head.RecordInfoHead;
import com.bjfu.fortree.excel.head.TreeInfoHead;
import com.bjfu.fortree.excel.head.WoodlandInfoHead;
import com.bjfu.fortree.exception.ExportExcelException;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.exception.WrongParamException;
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
import java.util.Optional;
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
    @Transactional(rollbackFor = RuntimeException.class)
    public void exportWoodlandDetailInfo(Long woodlandId, HttpServletResponse response) {
        Optional<Woodland> woodlandOptional = woodlandRepository.findById(woodlandId);
        if(woodlandOptional.isEmpty()) {
            throw new WrongParamException(ResultEnum.WOODLAND_NOT_EXIST);
        }
        Woodland woodland = woodlandOptional.get();
        String fileName = URLEncoder.encode(woodland.getName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20") + ".xlsx";
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        try{
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
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                WriteSheet recordSheet = EasyExcel.writerSheet("记录"+simpleDateFormat.format(record.getCreatedTime())).build();
                WriteTable recordInfoTable = EasyExcel.writerTable(0).needHead(Boolean.TRUE).head(RecordInfoHead.class).build();
                WriteTable treesInfoTable = EasyExcel.writerTable(1).needHead(Boolean.TRUE).head(TreeInfoHead.class).build();
                excelWriter.write(Collections.singletonList(new RecordInfoHead(record)), recordSheet, recordInfoTable);
                excelWriter.write(record.getTrees().stream().map(TreeInfoHead::new).collect(Collectors.toList()),
                        recordSheet, treesInfoTable);
            });
            excelWriter.finish();
        } catch (Exception e) {
            log.error("导出错误", e);
            throw new ExportExcelException(ResultEnum.FILE_EXPORT_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ApplyJobDTO exportWoodlandsInfo(String userAccount, List<Long> woodlandIds) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        StringBuilder stringBuilder = new StringBuilder();
        woodlandRepository.findAllById(woodlandIds)
                .forEach(woodland -> stringBuilder.append(woodland.getName()).append(','));
        String exportWoodlandsName = stringBuilder.toString();
        if(stringBuilder.capacity() > 250) {
            exportWoodlandsName = stringBuilder.substring(0, 250) + "...";
        }
        String applyParam = JSONObject.toJSONString(woodlandIds);
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.EXPORT_ANY_INFO)) {
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.EXPORT_WOODLANDS_INFO, applyParam,
                    exportWoodlandsName);
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作器来落库
            approvedOperationDispatch.asyncDispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.EXPORT_WOODLANDS_INFO, applyParam,
                    exportWoodlandsName);
            // 将需要审批的请求申请落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ApplyJobDTO exportWoodlandsInfoInBounds(String userAccount, ExportWoodlandsInBoundsRequest request) {
        Optional<User> userOptional = userRepository.findByAccount(userAccount);
        if(userOptional.isEmpty()) {
            throw new SystemWrongException(ResultEnum.USER_SESSION_WRONG);
        }
        User user = userOptional.get();
        StringBuilder stringBuilder = new StringBuilder();
        woodlandRepository.findWoodlandsInBounds(request.getPolygon().convertToGeom())
                .forEach(woodland -> stringBuilder.append(woodland.getName()).append(','));
        String exportWoodlandsName = stringBuilder.toString();
        if(stringBuilder.capacity() > 250) {
            exportWoodlandsName = stringBuilder.substring(0, 250) + "...";
        }
        String applyParam = JSONObject.toJSONString(request);
        if(authorityRepository.existsByUserAndType(user, AuthorityTypeEnum.EXPORT_ANY_INFO)) {
            ApplyJob passedApply = ApplyJob.createPassedApply(user, ApplyJobTypeEnum.EXPORT_WOODLANDS_INFO, applyParam,
                    exportWoodlandsName);
            applyJobRepository.save(passedApply);
            // 执行审批通过后的操作器来落库
            approvedOperationDispatch.asyncDispatch(passedApply);
            return new ApplyJobDTO(passedApply);
        } else {
            ApplyJob apply = ApplyJob.createApply(user, ApplyJobTypeEnum.EXPORT_WOODLANDS_INFO, applyParam,
                    exportWoodlandsName);
            // 将需要审批的请求申请落库
            applyJobRepository.save(apply);
            return new ApplyJobDTO(apply);
        }
    }

}
