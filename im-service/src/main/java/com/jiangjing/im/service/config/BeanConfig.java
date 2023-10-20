package com.jiangjing.im.service.config;

import com.jiangjing.im.common.config.AppConfig;
import com.jiangjing.im.common.enums.ImUrlRouteWayEnum;
import com.jiangjing.im.common.enums.RouteHashMethodEnum;
import com.jiangjing.im.common.route.RouteHandle;
import com.jiangjing.im.common.route.algorithm.consistenthash.AbstractConsistentHash;
import com.jiangjing.im.service.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * @author Admin
 */
@Configuration
public class BeanConfig {

    @Autowired
    AppConfig appConfig;

    /**
     * 封装用户登录时的路由策略
     *  1、根据配置文件配置的路由策略，获取路由实例
     *  2、如果是使用一致性hash算法，需要指定具体的实现
     *
     * @return
     * @throws Exception
     */
    @Bean
    public RouteHandle routeHandle() throws Exception {
        Integer imRouteWay = appConfig.getImRouteWay();
        ImUrlRouteWayEnum handler = ImUrlRouteWayEnum.getHandler(imRouteWay);
        assert handler != null;
        String routWay = handler.getClazz();
        // 反射获取的路由实例
        RouteHandle routeHandle = (RouteHandle) Class.forName(routWay).newInstance();
        if (handler == ImUrlRouteWayEnum.HASH) {
            Method setHash = Class.forName(routWay).getMethod("setHash", AbstractConsistentHash.class);
            Integer consistentHashWay = appConfig.getConsistentHashWay();
            // 获取一致性hash算法的实现
            RouteHashMethodEnum hashHandler = RouteHashMethodEnum.getHandler(consistentHashWay);
            assert hashHandler != null;
            String hashWay = hashHandler.getClazz();
            AbstractConsistentHash consistentHash
                    = (AbstractConsistentHash) Class.forName(hashWay).newInstance();
            setHash.invoke(routeHandle, consistentHash);
        }
        return routeHandle;
    }

    /**
     * mybatis plus 批量插入的配置
     *
     * @return
     */
    @Bean
    public EasySqlInjector easySqlInjector () {
        return new EasySqlInjector();
    }

    /***
     * 雪花算法
     *
     * @return
     * @throws Exception
     */
    @Bean
    public SnowflakeIdWorker buildSnowflakeSeq() throws Exception {
        return new SnowflakeIdWorker(0);
    }

}
