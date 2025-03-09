package com.example.imtcp.server;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.example.imtcp.config.ImConfigInfo;
import com.example.imtcp.handler.HeartBeatServerHandler;
import com.example.imtcp.handler.NettyServerHandler;
import com.jiangjing.WebSocketMessageDecoder;
import com.jiangjing.WebSocketMessageEncoder;
import com.jiangjing.im.common.constant.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
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
import java.net.UnknownHostException;

/**
 * @author jingjing
 * @date 2023/6/16 20:31
 */
@Component
public class ImWebSocketServer implements ApplicationListener<ApplicationEvent> {
    private final static Logger logger = LoggerFactory.getLogger(ImWebSocketServer.class);

    @Autowired
    private ImConfigInfo imConfigInfo;

    @Autowired
    ObjectProvider<NettyServerHandler> nettyServerHandlerProvider;

    @Autowired
    ObjectProvider<HeartBeatServerHandler> heartBeatServerHandlerObjectProvider;

    @Autowired
    NacosDiscoveryProperties nacosDiscoveryProperties;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture serverChannelFuture;
    private NamingService naming;

    /**
     * 获取用于注册的IP地址
     * 优先使用环境变量或系统属性中的HOST_IP，如果没有则使用本地IP
     */
    private String getRegistrationIp() throws UnknownHostException {
        // 获取宿主机 IP，优先从环境变量获取，如果没有则使用本地 IP
        String hostIp = System.getenv("HOST_IP");
        logger.info("获取环境变量的地址为：{}", hostIp);
        if (hostIp == null || hostIp.isEmpty()) {
            hostIp = InetAddress.getLocalHost().getHostAddress();
            logger.info("获取环境变量的地址为空，获取容器的地址为：{}", hostIp);
        }
        return hostIp;
    }

    /**
     * Spring 容器启动成功之后会调用该方法
     */
    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 容器启动完成时，开启服务端
        if (event instanceof ContextRefreshedEvent) {
            bossGroup = new NioEventLoopGroup(imConfigInfo.getBossThreadSize());
            workerGroup = new NioEventLoopGroup(imConfigInfo.getWorkThreadSize());
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 10240) // 服务端可连接队列大小
                    .option(ChannelOption.SO_REUSEADDR, true) // 参数表示允许重复使用本地地址和端口
                    .childOption(ChannelOption.TCP_NODELAY, true) // 是否禁用Nagle算法 简单点说是否批量发送数据 true关闭 false开启。 开启的话可以减少一定的网络开销，但影响消息实时性
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 保活开关2h没有数据服务端会发送心跳包
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        /**
                         * 初始化channel，客户端连接成功时回调
                         */
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // websocket 基于http协议，所以要有http编解码器
                            pipeline.addLast("http-codec", new HttpServerCodec());
                            // 对写大数据流的支持
                            pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                            // 几乎在netty中的编程，都会使用到此 handler
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
                            /**
                             * websocket 服务器处理的协议，用于指定给客户端连接访问的路由 : /ws
                             * 本handler会帮你处理一些繁重的复杂的事
                             * 会帮你处理握手动作： handshaking（close, ping, pong） ping + pong = 心跳
                             * 对于websocket来讲，都是以frames进行传输的，不同的数据类型对应的frames也不同
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                            pipeline.addLast(new WebSocketMessageDecoder());
                            pipeline.addLast(new WebSocketMessageEncoder());
                            socketChannel.pipeline().addLast(new IdleStateHandler(20, 0, 0));
                            socketChannel.pipeline().addLast(heartBeatServerHandlerObjectProvider.getObject());
                            pipeline.addLast(nettyServerHandlerProvider.getObject());
                        }
                    });
            serverChannelFuture = serverBootstrap.bind(imConfigInfo.getWebSocketPort()).sync();

            // 向 Nacos 发起注册
            naming = NamingFactory.createNamingService(nacosDiscoveryProperties.getServerAddr());
            String registrationIp = getRegistrationIp();
            naming.registerInstance(Constants.IM_NACOS_SERVICE_WEB, registrationIp, imConfigInfo.getWebSocketPort(), "DEFAULT");
            logger.info("WebSocket server started,bind port is " + imConfigInfo.getWebSocketPort());
        } else if (event instanceof ContextClosedEvent) {
            // 容器关闭时，关闭服务端
            serverChannelFuture.channel().close();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            String registrationIp = getRegistrationIp();
            naming.deregisterInstance(Constants.IM_NACOS_SERVICE_WEB, registrationIp, imConfigInfo.getWebSocketPort(), "DEFAULT");
            logger.info("WebSocket server closed,port:" + imConfigInfo.getTcpPort());
        }
    }
}
