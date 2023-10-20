package com.example.imtcp.server;

import com.example.imtcp.config.ImConfigInfo;
import com.example.imtcp.handler.NettyServerHandler;
import com.jiangjing.WebSocketMessageDecoder;
import com.jiangjing.WebSocketMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

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
    NettyServerHandler nettyServerHandler;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture serverChannelFuture;


    /**
     * Spring 容器启动成功之后会调用该方法
     *
     * @param event
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
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // websocket 基于http协议，所以要有http编解码器
                            pipeline.addLast("http-codec", new HttpServerCodec());
                            // 对写大数据流的支持
                            pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                            // 几乎在netty中的编程，都会使用到此hanler
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
                            pipeline.addLast(nettyServerHandler);
                        }
                    });
            serverChannelFuture = serverBootstrap.bind(imConfigInfo.getWebSocketPort()).sync();
            logger.info("WebSocket server started,bind port is " + imConfigInfo.getWebSocketPort());
        } else if (event instanceof ContextClosedEvent) {
            // 容器关闭时，关闭服务端
            serverChannelFuture.channel().close();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("WebSocket server closed,port:" + imConfigInfo.getTcpPort());
        }
    }
}
