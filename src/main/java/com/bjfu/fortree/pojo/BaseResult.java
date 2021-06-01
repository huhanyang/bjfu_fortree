package com.bjfu.fortree.pojo;

import com.bjfu.fortree.enums.ResultEnum;
import lombok.Data;

@Data
public class BaseResult<T> {

    private int code;
    private String msg;
    private T object;

    public BaseResult(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public BaseResult(ResultEnum resultEnum, T object) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
        this.object = object;
    }

    public BaseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
