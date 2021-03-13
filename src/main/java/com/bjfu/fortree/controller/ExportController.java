package com.bjfu.fortree.controller;

import com.bjfu.fortree.pojo.dto.job.ApplyJobDTO;
import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.exception.ExportExcelException;
import com.bjfu.fortree.pojo.request.export.ExportWoodlandsInfoRequest;
import com.bjfu.fortree.security.annotation.RequireLogin;
import com.bjfu.fortree.service.ExportService;
import com.bjfu.fortree.util.ResponseUtil;
import com.bjfu.fortree.util.SessionUtil;
import com.bjfu.fortree.pojo.vo.BaseResult;
import com.bjfu.fortree.pojo.vo.apply.ApplyJobVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * 导出相关操作接口
 * @author warthog
 */
@RestController
@RequestMapping("/export")
@Validated
public class ExportController {

    @Autowired
    private ExportService exportService;

    @RequireLogin
    @GetMapping("/exportWoodlandDetailInfo")
    public void exportWoodlandDetailInfo(@NotNull(message = "林地id不能为空!") Long id, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里需要设置不关闭流
            exportService.exportWoodlandDetailInfo(id, response);
        } catch (ExportExcelException e) {
            // 重置response
            ResponseUtil.writeResultToResponse(ResultEnum.FILE_EXPORT_FAILED, response);
        }
    }

    @RequireLogin
    @PostMapping("/exportWoodlandsInfo")
    public BaseResult<ApplyJobVO> exportWoodlandsInfo(@Validated @RequestBody ExportWoodlandsInfoRequest exportWoodlandsInfoRequest,
                                                      HttpSession session) {
        ApplyJobDTO applyJobDTO = exportService.exportWoodlandsInfo(SessionUtil.getUserInfo(session).getAccount(),
                exportWoodlandsInfoRequest.getIds());
        return new BaseResult<>(ResultEnum.SUCCESS, new ApplyJobVO(applyJobDTO));
    }
}
