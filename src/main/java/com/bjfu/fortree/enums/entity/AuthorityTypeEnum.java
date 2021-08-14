package com.bjfu.fortree.enums.entity;

import lombok.Getter;

/**
 * 用户权限枚举类
 *
 * @author warthog
 */
@Getter
public enum AuthorityTypeEnum {
    /**
     * 创建林地无需审批
     */
    CREATE_ANY_WOODLAND,
    /**
     * 添加任何林地中的记录无需审批
     */
    ADD_RECORD_IN_ANY_WOODLAND,
    /**
     * 添加任何记录中的树木无需审批
     */
    ADD_TREES_IN_ANY_RECORD,
    /**
     * 删除所有林地
     */
    DELETE_ANY_WOODLAND,
    /**
     * 删除任何林地中的记录无需审批
     */
    DELETE_RECORD_IN_ANY_WOODLAND,
    /**
     * 删除任何记录中的树木无需审批
     */
    DELETE_TREES_IN_ANY_RECORD,
    /**
     * 编辑任何林地的信息无需审批
     */
    EDIT_ANY_WOODLAND,
    /**
     * 编辑任何林地中的记录无需审批
     */
    EDIT_RECORD_IN_ANY_WOODLAND,
    /**
     * 导出任何信息无需审批
     */
    EXPORT_ANY_INFO,
}
