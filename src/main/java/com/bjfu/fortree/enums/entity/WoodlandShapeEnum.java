package com.bjfu.fortree.enums.entity;

import lombok.Getter;

/**
 * 林地形状枚举类
 * @author warthog
 */

@Getter
public enum WoodlandShapeEnum {
    /**
     * 正方形
     */
    SQUARE("正方形"),
    /**
     * 长方形
     */
    RECTANGLE("长方形"),
    /**
     * 圆形
     */
    CIRCULAR("圆形");

    private final String msg;

    WoodlandShapeEnum(String msg) {
        this.msg = msg;
    }

}
