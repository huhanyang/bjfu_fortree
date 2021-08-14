package com.bjfu.fortree.exception;

import com.bjfu.fortree.enums.ResultEnum;

/**
 * 系统错误异常
 *
 * @author warthog
 */
public class SystemWrongException extends ForTreeException {
    public SystemWrongException(ResultEnum resultEnum) {
        super(resultEnum);
    }
}
