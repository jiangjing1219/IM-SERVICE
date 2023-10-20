package com.jiangjing.im.common.enums;

/**
 * @author jingjing
 * @date 2023/6/24 11:05
 */
public enum ImConnectStatusEnum {
    /**
     * 管道链接状态,1=在线，2=离线。。
     */
    ONLINE_STATUS(1),

    OFFLINE_STATUS(2),
    ;

    private final Integer code;

    ImConnectStatusEnum(Integer code){
        this.code=code;
    }

    public Integer getCode() {
        return code;
    }
}
