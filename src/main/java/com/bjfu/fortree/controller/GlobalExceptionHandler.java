package com.bjfu.fortree.controller;

import com.bjfu.fortree.enums.ResultEnum;
import com.bjfu.fortree.exception.ApprovedOperationException;
import com.bjfu.fortree.exception.ForTreeException;
import com.bjfu.fortree.exception.OssException;
import com.bjfu.fortree.exception.SystemWrongException;
import com.bjfu.fortree.pojo.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;

/**
 * 全局异常处理器
 *
 * @author warthog
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResult<Void> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return new BaseResult<>(ResultEnum.PARAM_WRONG.getCode(),
                Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResult<Void> constraintViolationException(ConstraintViolationException ex) {
        return new BaseResult<>(ResultEnum.PARAM_WRONG.getCode(),
                ex.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage).orElse("参数错误"));
    }

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
