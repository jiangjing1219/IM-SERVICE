package com.example.imtcp.server;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.example.imtcp.config.ImConfigInfo;
import com.example.imtcp.handler.HeartBeatServerHandler;
import com.example.imtcp.handler.NettyServerHandler;
import com.jiangjing.MessageDecoder;
import com.jiangjing.MessageEncoder;
import com.jiangjing.im.common.constant.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * @author jingjing
 * @date 2023/6/16 20:31
 */
@Component
public class ImNettyServer implements ApplicationListener<ApplicationEvent> {
    private final static Logger logger = LoggerFactory.getLogger(ImNettyServer.class);

    @Autowired
    ImConfigInfo imConfigInfo;

    @Autowired
    ObjectProvider<NettyServerHandler> nettyServerHandlerProvider;

    @Autowired
    ObjectProvider<HeartBeatServerHandler> heartBeatServerHandlerObjectProvider;

    @Autowired
    NacosDiscoveryProperties nacosDiscoveryProperties;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture serverChannelFuture;

    NamingService naming;

    /**
     * Spring 容器启动成功之后会调用该方法
     *
     * @param event
     */
    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            bossGroup = new NioEventLoopGroup(imConfigInfo.getBossThreadSize());
            workerGroup = new NioEventLoopGroup(imConfigInfo.getWorkThreadSize());
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 服务端可连接队列大小
                    .option(ChannelOption.SO_BACKLOG, 10240)
                    // 参数表示允许重复使用本地地址和端口
                    .option(ChannelOption.SO_REUSEADDR, true)
                    // 是否禁用Nagle算法 简单点说是否批量发送数据 true关闭 false开启。 开启的话可以减少一定的网络开销，但影响消息实时性
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 保活开关2h没有数据服务端会发送心跳包
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MessageDecoder());
                            socketChannel.pipeline().addLast(new MessageEncoder());
                            socketChannel.pipeline().addLast(new IdleStateHandler(3000, 0, 0));
                            socketChannel.pipeline().addLast(heartBeatServerHandlerObjectProvider.getObject());
                            socketChannel.pipeline().addLast(nettyServerHandlerProvider.getObject());
                        }
                    });
            serverChannelFuture = serverBootstrap.bind(imConfigInfo.getTcpPort()).sync();
            logger.info("Netty server started, bind port is " + imConfigInfo.getTcpPort());
            // 向 Nacos 发起注册
            naming = NamingFactory.createNamingService(nacosDiscoveryProperties.getServerAddr());
            naming.registerInstance(Constants.IM_NACOS_SERVICE_TCP, InetAddress.getLocalHost().getHostAddress(), imConfigInfo.getTcpPort(), "DEFAULT");
        } else if (event instanceof ContextClosedEvent) {
            serverChannelFuture.channel().close();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            // 取消注册
            naming.deregisterInstance(Constants.IM_NACOS_SERVICE_TCP, InetAddress.getLocalHost().getHostAddress(), imConfigInfo.getTcpPort(), "DEFAULT");
            logger.info("Netty server closed, port:{}", imConfigInfo.getTcpPort());
        }
    }
}
