package com.bjfu.fortree.enums.entity;

import lombok.Getter;

@Getter
public enum RecordTypeEnum {

    AUTO_CAL("自动计算"),
    USER_EDIT("用户编辑");

    private final String msg;

    RecordTypeEnum(String msg) {
        this.msg = msg;
    }
}
