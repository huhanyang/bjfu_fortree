package com.bjfu.fortree.enums.entity;

import lombok.Getter;

/**
 * 申请状态枚举类
 * @author warthog
 */
@Getter
public enum ApplyJobStateEnum {
    /**
     * 申请中
     */
    APPLYING,
    /**
     * 撤销申请
     */
    CANCELLED,
    /**
     * 申请通过
     */
    PASSED,
    /**
     * 申请通过执行成功
     */
    PASSED_EXECUTION_SUCCESS,
    /**
     * 申请通过但后续操作执行失败
     */
    PASSED_EXECUTION_FAILED,
    /**
     * 申请未通过
     */
    NOT_PASSED
}
