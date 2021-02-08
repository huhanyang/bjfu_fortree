package com.bjfu.fortree.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页VO
 * @author warthog
 */
@Data
public class PageVO<T> {

    public PageVO(Long count, List<T> contents){
        this.count = count;
        this.contents = contents;
    }

    /**
     * 所有页的内容总和
     */
    private Long count;
    /**
     * 此页的内容
     */
    private List<T> contents;
}
