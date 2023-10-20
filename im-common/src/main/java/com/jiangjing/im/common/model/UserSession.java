package com.jiangjing.im.common.model;

import lombok.Data;

/**
 * @author jingjing
 * @date 2023/6/24 11:00
 */
@Data
public class UserSession {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 端的标识
     */
    private Integer clientType;

    //sdk 版本号
    private Integer version;

    //连接状态 1=在线 2=离线
    private Integer connectState;

    /**
     * 服务器标识
     */
    private Integer brokerId;

    /**
     * 服务器id
     */
    private String brokerHost;

    /**
     * 设备号
     */
    private String imei;
}
