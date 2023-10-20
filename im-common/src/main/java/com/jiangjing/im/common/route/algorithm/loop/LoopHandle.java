package com.jiangjing.im.common.route.algorithm.loop;

import com.jiangjing.im.common.enums.UserErrorCode;
import com.jiangjing.im.common.exception.ApplicationException;
import com.jiangjing.im.common.route.RouteHandle;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 轮询获取，感觉最好的解决方法就是，根据 Redis 的计数器，求余.  简单实现
 *
 * @author Admin
 */
public class LoopHandle implements RouteHandle {
    private AtomicLong index = new AtomicLong();
    @Override
    public String routeServer(List<String> values, String key) {
        int size = values.size();
        if(size == 0){
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        Long l = index.incrementAndGet() % size;
        if(l < 0){
            l = 0L;
        }
        return values.get(l.intValue());
    }
}
