package com.bjfu.fortree.controller;

import com.bjfu.fortree.exception.*;
import com.bjfu.fortree.pojo.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理器
 * @author warthog
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ForTreeException.class)
    public BaseResult<Void> forTreeException(ForTreeException forTreeException) {
        return new BaseResult<>(forTreeException.getResultEnum());
    }

    @ResponseBody
    @ExceptionHandler(ApprovedOperationException.class)
    public BaseResult<Void> forTreeException(ApprovedOperationException exception) {
        log.error("ApprovedOperationException: ", exception);
        return new BaseResult<>(exception.getResultEnum());
    }

    @ResponseBody
    @ExceptionHandler(OssException.class)
    public BaseResult<Void> forTreeException(OssException exception) {
        log.error("OssException: ", exception);
        return new BaseResult<>(exception.getResultEnum());
    }

    @ResponseBody
    @ExceptionHandler(SystemWrongException.class)
    public BaseResult<Void> forTreeException(SystemWrongException exception) {
        log.error("SystemWrongException: ", exception);
        return new BaseResult<>(exception.getResultEnum());
    }

}
