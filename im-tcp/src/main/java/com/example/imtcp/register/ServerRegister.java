package com.example.imtcp.register;


import com.example.imtcp.config.ImConfigInfo;
import com.example.imtcp.server.ImNettyServer;
import com.jiangjing.im.common.constant.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * @author Admin
 */
@Component
public class ServerRegister implements ApplicationListener<ContextRefreshedEvent> {

    private final static Logger logger = LoggerFactory.getLogger(ImNettyServer.class);

    @Autowired
    private ImConfigInfo imConfigInfo;

    private final CuratorFramework curatorFramework;

    /**
     * 目前时推荐组件使用构造组注入的方式：能够提供更好的可读性、可维护性和可控性，同时也能够避免空指针异常，并且不会导致循环依赖问题
     * <p>
     * 显式依赖：通过构造方法注入，可以明确标识出组件所依赖的其他组件，并在构造组件实例时传递这些依赖项。这样可以清晰地表示组件之间的关系，并提供更好的可读性和可维护性。
     * <p>
     * 不可变性：使用构造方法注入后，被注入的依赖项可以在组件实例化后保持不变，使得组件的状态更加可控。这有助于提高代码的稳定性和可测试性。
     * <p>
     * 避免空指针异常：通过在构造方法中注入依赖项，可以确保在使用该组件时依赖项不为 null。这样可以避免空指针异常，并提供更好的代码安全性。
     *
     * @param curatorFramework
     */
    @Autowired
    public ServerRegister(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    /**
     * 容器启动完成，向 zookeeper 注册当前服务
     * -ImCoreRoot
     * -tcp
     * - ip:port
     * -web
     * - ip:port
     *
     * @param contextRefreshedEvent
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String tcpPath = Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_TCP + "/" + localHost.getHostAddress() + ":" + imConfigInfo.getTcpPort();
            // 创建临时节点
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(tcpPath, null);
            logger.info("Registry zookeeper tcpPath success, msg=[{}]", tcpPath);
            String webPath = Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_WEB + "/" + localHost.getHostAddress() + ":" + imConfigInfo.getWebSocketPort();
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(webPath, null);
            logger.info("Registry zookeeper tcpPath success, msg=[{}]", webPath);
        } catch (Exception e) {
            System.out.println("Registry zookeeper tcpPath exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
