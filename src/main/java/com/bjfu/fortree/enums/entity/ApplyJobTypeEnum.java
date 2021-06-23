package com.bjfu.fortree.enums.entity;

import com.bjfu.fortree.approval.ApprovedOperation;
import com.bjfu.fortree.approval.operation.*;
import lombok.Getter;

/**
 * 申请类型的枚举类
 * @author warthog
 */
@Getter
public enum ApplyJobTypeEnum {

    /**
     * 创建林地
     */
    CREATE_WOODLAND(new CreateWoodlandApprovedOperation()),
    ADD_RECORD_IN_WOODLAND(new AddRecordInWoodlandOperation()),
    ADD_TREES_IN_RECORD(new AddTreesInRecordOperation()),
    ADD_TREES_BY_EXCEL_IN_RECORD(new AddTreesByExcelInRecordOperation()),
    DELETE_WOODLAND(new DeleteWoodlandOperation()),
    DELETE_RECORD_IN_WOODLAND(new DeleteRecordInWoodlandOperation()),
    DELETE_TREES_IN_RECORD(new DeleteTreesInRecordOperation()),
    EDIT_WOODLAND(new EditWoodlandOperation()),
    EDIT_RECORD(new EditRecordOperation()),
    EXPORT_WOODLANDS_INFO(new ExportWoodlandsInfoOperation()),
    EXPORT_WOODLANDS_IN_BOUNDS(new ExportWoodlandsInBoundsOperation()),
    CONFIRM_REGISTER(new ConfirmRegisterOperation());

    /**
     * 申请通过的后置执行类
     */
    private final Class<ApprovedOperation> approvedOperationClass;

    ApplyJobTypeEnum(ApprovedOperation approvedOperation) {
        this.approvedOperationClass = (Class<ApprovedOperation>) approvedOperation.getClass();
    }
}
