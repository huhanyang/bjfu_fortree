package com.bjfu.fortree.pojo.request.export;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author warthog
 */
@Data
public class ExportWoodlandsInfoRequest {
    @NotEmpty(message = "林地id不能为空!")
    private List<Long> ids;
}
