package com.bjfu.fortree.exception;

import com.bjfu.fortree.enums.ResultEnum;
import lombok.Getter;

/**
 * 应用异常抽象类
 *
 * @author warthog
 */
@Getter
public abstract class ForTreeException extends RuntimeException {

    private final ResultEnum resultEnum;

    public ForTreeException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.resultEnum = resultEnum;
    }

}
