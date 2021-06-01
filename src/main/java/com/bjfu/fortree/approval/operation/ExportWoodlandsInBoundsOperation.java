package com.bjfu.fortree.approval.operation;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.fastjson.JSONObject;
import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.config.MinioConfig;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.excel.head.RecordInfoHead;
import com.bjfu.fortree.excel.head.TreeInfoHead;
import com.bjfu.fortree.excel.head.WoodlandInfoHead;
import com.bjfu.fortree.exception.ExportExcelException;
import com.bjfu.fortree.pojo.entity.ApplyJob;
import com.bjfu.fortree.pojo.entity.OssFile;
import com.bjfu.fortree.pojo.entity.User;
import com.bjfu.fortree.pojo.entity.Woodland;
import com.bjfu.fortree.pojo.request.export.ExportWoodlandsInBoundsRequest;
import com.bjfu.fortree.repository.file.OssFileRepository;
import com.bjfu.fortree.repository.woodland.WoodlandRepository;
import com.bjfu.fortree.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportWoodlandsInBoundsOperation implements ApprovedOperation {

    @Autowired
    private WoodlandRepository woodlandRepository;
    @Autowired
    private OssFileRepository ossFileRepository;
    @Autowired
    private OssService ossService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = RuntimeException.class)
    public void execute(ApplyJob applyJob, User applyUser) {
        ExportWoodlandsInBoundsRequest request = JSONObject.parseObject(applyJob.getApplyParam(), ExportWoodlandsInBoundsRequest.class);
        List<Woodland> woodlands = woodlandRepository.findAllInBounds(request.getPolygon().convertToGeom());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            ExcelWriter excelWriter = EasyExcel.write(outputStream).autoCloseStream(false).build();
            // 写入林地信息sheet
            WriteSheet woodlandSheet = EasyExcel.writerSheet("林地信息").build();
            WriteTable woodlandInfoTable = EasyExcel.writerTable(0).needHead(Boolean.TRUE).head(WoodlandInfoHead.class).build();
            excelWriter.write(woodlands.stream().map(WoodlandInfoHead::new).collect(Collectors.toList()), woodlandSheet, woodlandInfoTable);
            // 写入多个记录信息sheet
            woodlands.forEach(woodland -> {
                woodland.getRecords().stream().findFirst().ifPresent(record -> {
                    WriteSheet recordSheet = EasyExcel.writerSheet(woodland.getName() + "最新记录").build();
                    WriteTable recordInfoTable = EasyExcel.writerTable(0).needHead(Boolean.TRUE)
                            .head(RecordInfoHead.class).build();
                    WriteTable treesInfoTable = EasyExcel.writerTable(1).needHead(Boolean.TRUE)
                            .head(TreeInfoHead.class).build();
                    excelWriter.write(Collections.singletonList(new RecordInfoHead(record)),
                            recordSheet, recordInfoTable);
                    excelWriter.write(record.getTrees().stream().map(TreeInfoHead::new).collect(Collectors.toList()),
                            recordSheet, treesInfoTable);
                });
            });
            excelWriter.finish();
            // 生成的excel上传oss
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            String ossObjectName = UUID.randomUUID().toString();
            ossService.putObject(MinioConfig.EXCEL_BUCKET_NAME, ossObjectName, inputStream);
            // 记录上传oss的文件信息
            OssFile ossFile = new OssFile();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd导出文件");
            ossFile.setFileName(format.format(new Date()) + ".xlsx");
            ossFile.setOssBucketName(MinioConfig.EXCEL_BUCKET_NAME);
            ossFile.setOssObjectName(ossObjectName);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            ossFile.setExpiresTime(calendar.getTime());
            ossFileRepository.save(ossFile);
            // 将上传的文件信息保存到申请中
            applyJob.setDownloadFile(ossFile);
            applyJob.setMsg("文件导出成功");
        } catch (Exception e) {
            log.error("导出错误", e);
            throw new ExportExcelException(ResultEnum.FILE_EXPORT_FAILED);
        }
    }
}
