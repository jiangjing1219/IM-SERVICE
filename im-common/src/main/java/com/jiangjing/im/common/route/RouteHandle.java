package com.jiangjing.im.common.route;

import java.util.List;

/**
 * @author Admin
 */
public interface RouteHandle {

    /**
     * 路由服务
     *
     * @param values  服务地址集合
     * @param key  路由依据key
     * @return
     */
    String routeServer(List<String> values, String key);
}
