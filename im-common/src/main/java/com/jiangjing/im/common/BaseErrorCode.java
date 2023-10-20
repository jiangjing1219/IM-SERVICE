package com.jiangjing.im.common;


import com.jiangjing.im.common.exception.ApplicationExceptionEnum;

/**
 * @author Admin
 */
public enum BaseErrorCode implements ApplicationExceptionEnum {
    SUCCESS(200, "Success"),
    PARAMETER_ERROR(201, "Parameter Error"),
    SYSTEM_ERROR(9001, "System Error"),
    ;

    private int code;

    private String msg;

    BaseErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getError() {
        return msg;
    }
}
