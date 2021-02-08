package com.bjfu.fortree.request.woodland;

import lombok.Data;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author warthog
 */
@Data
public class GetWoodlandByCreatorRequest {
    //分页
    @NotNull(message = "分页当前页数不能为空")
    private Integer current;
    @NotNull(message = "分页每页的数量不能为空")
    private Integer pageSize;
    //过滤
    private List<String> name;
    private List<String> country;
    private List<String> province;
    private List<String> city;
    //排序
    private String field;
    private Sort.Direction order;
}
