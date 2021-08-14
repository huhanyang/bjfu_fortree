package com.bjfu.fortree.exception;

import com.bjfu.fortree.enums.ResultEnum;

/**
 * 审批通过后的操作器执行中出现的异常
 *
 * @author warthog
 */
public class ApprovedOperationException extends ForTreeException {
    public ApprovedOperationException(ResultEnum resultEnum) {
        super(resultEnum);
    }
}
