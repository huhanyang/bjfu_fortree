package com.bjfu.fortree.service;

import com.bjfu.fortree.pojo.dto.job.ApplyJobDTO;
import com.bjfu.fortree.pojo.request.export.ExportWoodlandsInBoundsRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 导出相关操作
 * @author warthog
 */
public interface ExportService {

    /**
     * 将林地详细信息导出成excel
     * @param woodlandId 林地id
     * @param response http响应（用于直接写入文件）
     */
    void exportWoodlandDetailInfo(Long woodlandId, HttpServletResponse response);

    /**
     * 申请将选中的林地及林地记录信息导出成excel
     * @param userAccount 用户账号
     * @param woodlandIds 林地id列表
     * @return 申请实体
     */
    ApplyJobDTO exportWoodlandsInfo(String userAccount, List<Long> woodlandIds);

    /**
     * 申请将范围内的林地及林地记录信息导出成excel
     * @param userAccount 用户账号
     * @param request 请求参数
     * @return 申请实体
     */
    ApplyJobDTO exportWoodlandsInfoInBounds(String userAccount, ExportWoodlandsInBoundsRequest request);
}
