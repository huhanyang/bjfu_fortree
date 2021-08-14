package com.bjfu.fortree.exception;

import com.bjfu.fortree.enums.ResultEnum;

/**
 * oss操作时发生的异常
 *
 * @author warthog
 */
public class OssException extends ForTreeException {
    public OssException(ResultEnum resultEnum) {
        super(resultEnum);
    }
}
