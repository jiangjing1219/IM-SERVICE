package com.jiangjing.im.common.route;

import lombok.Data;

/**
 * 路由信息
 *
 * @author Admin
 */
@Data
public class RouteInfo {

    private int port;

    private String ip;

    public RouteInfo(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }
}
