package com.jiangjing.im.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Chackylee
 * @description:
 **/
@Data
@NoArgsConstructor
public class ClientInfo {

    /**
     * appid
     */
    private Integer appId;

    /**
     * 设备端类型
     */
    private Integer clientType;

    /**
     * 设备标识
     */
    private String imei;

    public ClientInfo(Integer appId, Integer clientType, String imei) {
        this.appId = appId;
        this.clientType = clientType;
        this.imei = imei;
    }
}
