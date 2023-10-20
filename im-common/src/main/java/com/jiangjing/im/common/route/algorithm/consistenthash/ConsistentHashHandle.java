package com.jiangjing.im.common.route.algorithm.consistenthash;

import com.jiangjing.im.common.route.RouteHandle;

import java.util.List;

/**
 * 使用时需要注入指定的 Hash 算法
 *
 * @author Admin
 */
public class ConsistentHashHandle implements RouteHandle {

    private AbstractConsistentHash hash;

    public void setHash(AbstractConsistentHash hash) {
        this.hash = hash;
    }

    @Override
    public String routeServer(List<String> values, String key) {
        return hash.process(values, key);
    }
}
