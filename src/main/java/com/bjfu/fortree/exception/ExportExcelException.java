package com.bjfu.fortree.exception;

import com.bjfu.fortree.enums.ResultEnum;

public class ExportExcelException extends ForTreeException {
    public ExportExcelException(ResultEnum resultEnum) {
        super(resultEnum);
    }
}
