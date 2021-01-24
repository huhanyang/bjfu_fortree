package com.bjfu.fortree.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {
    private Long count;
    private List<T> contents;
}
