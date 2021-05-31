package com.bjfu.fortree.exception;

import com.bjfu.fortree.enums.ResultEnum;

public class BizException extends ForTreeException {

    public BizException(ResultEnum resultEnum) {
        super(resultEnum);
    }
}
