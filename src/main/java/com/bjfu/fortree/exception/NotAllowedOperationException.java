package com.bjfu.fortree.exception;

import com.bjfu.fortree.enums.ResultEnum;

/**
 * 不允许的操作
 * @author warthog
 */
public class NotAllowedOperationException extends ForTreeException {
    public NotAllowedOperationException(ResultEnum resultEnum) {
        super(resultEnum);
    }
}
