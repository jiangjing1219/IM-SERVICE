package com.jiangjing.im.common.enums;


import com.jiangjing.im.common.exception.ApplicationExceptionEnum;

/**
 *
 * 用户导入的错误信息的枚举类型
 * @author Admin
 */

public enum UserErrorCode implements ApplicationExceptionEnum {


    IMPORT_SIZE_BEYOND(20000, "导入数量超出上限"),
    USER_IS_NOT_EXIST(20001, "用户不存在"),
    SERVER_GET_USER_ERROR(20002, "服务获取用户失败"),
    MODIFY_USER_ERROR(20003, "更新用户失败"),
    SUB_TIME_ERROR(20004, "订阅时间失效"),
    SERVER_NOT_AVAILABLE(71000, "没有可用的服务"),
    ;

    private int code;
    private String error;

    UserErrorCode(int code, String error) {
        this.code = code;
        this.error = error;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getError() {
        return this.error;
    }

}
