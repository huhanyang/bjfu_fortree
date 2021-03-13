package com.bjfu.fortree.controller;

import com.bjfu.fortree.exception.WrongParamException;
import com.bjfu.fortree.pojo.vo.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理器
 * @author warthog
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(WrongParamException.class)
    public BaseResult<Void> badParamException(WrongParamException wrongParamException){
        return new BaseResult<>(wrongParamException.getResultEnum());
    }

}
