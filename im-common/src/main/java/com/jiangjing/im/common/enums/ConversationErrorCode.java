package com.jiangjing.im.common.enums;


import com.jiangjing.im.common.exception.ApplicationExceptionEnum;

/**
 * @author: Chackylee
 * @description:
 **/
public enum ConversationErrorCode implements ApplicationExceptionEnum {

    CONVERSATION_UPDATE_PARAM_ERROR(50000, "会话修改参数错误"),


    ;

    private int code;
    private String error;

    ConversationErrorCode(int code, String error) {
        this.code = code;
        this.error = error;
    }

    public int getCode() {
        return this.code;
    }

    public String getError() {
        return this.error;
    }

}
