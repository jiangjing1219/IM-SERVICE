package com.jiangjing.im.common.enums;

import lombok.Data;

/**
 * @author Admin
 *
 * 客户端类型
 */


public enum ClientType {
    WEBAPI(0, "webapi"),
    WEB(1, "web"),
    IOS(2, "ios"),
    ANDROID(3, "android"),
    MAC(4, "mac"),
    WINDOWS(5, "windows");


    private int code;
    private String description;


    ClientType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
