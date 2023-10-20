package com.jiangjing.im.common.model;

import lombok.Data;

/**
 * 基础的请求信息
 *
 * @author Admin
 */
@Data
public class RequestBase {

    private Integer appId;

    private String operate;

    private Integer clientType;

    private String imei;
}
