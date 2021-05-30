package com.bjfu.fortree.pojo.request.woodland;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AddTreesByExcelRequest {
    /**
     * 所属记录
     */
    @NotNull(message = "记录的id不能为空")
    private Long recordId;
    /**
     * 文件名
     */
    @NotEmpty(message = "文件名不能为空")
    private String fileName;
    /**
     * 文件
     */
    @NotNull(message = "文件不能为空!")
    private MultipartFile file;
}
