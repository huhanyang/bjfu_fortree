package com.bjfu.fortree.exception;

import com.bjfu.fortree.enums.ResultEnum;

/**
 * 参数异常
 * @author warthog
 */
public class WrongParamException extends ForTreeException {
    public WrongParamException(ResultEnum resultEnum) {
        super(resultEnum);
    }
}
