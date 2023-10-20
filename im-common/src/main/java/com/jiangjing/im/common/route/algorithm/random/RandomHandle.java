package com.jiangjing.im.common.route.algorithm.random;

import com.jiangjing.im.common.enums.UserErrorCode;
import com.jiangjing.im.common.exception.ApplicationException;
import com.jiangjing.im.common.route.RouteHandle;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Admin
 */
public class RandomHandle implements RouteHandle {

    /**
     * 随机路由
     * @param values  服务地址集合
     * @param key  路由依据key
     * @return
     */
    @Override
    public String routeServer(List<String> values, String key) {
        int size = values.size();
        if(size == 0){
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        /**
         * 现成安全的获取随机数  [0,n)
         */
        int nextInt = ThreadLocalRandom.current().nextInt(size);
        return values.get(nextInt);
    }
}
