package com.bjfu.fortree.exception;

import com.bjfu.fortree.enums.ResultEnum;

/**
 * 未授权的操作
 * @author warthog
 */
public class UnauthorizedOperationException extends ForTreeException {
    public UnauthorizedOperationException(ResultEnum resultEnum) {
        super(resultEnum);
    }
}
