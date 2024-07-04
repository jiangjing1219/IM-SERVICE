package com.example.imtcp.handler;

import com.example.imtcp.config.ImConfigInfo;
import com.example.imtcp.utils.SessionSocketHolder;
import com.jiangjing.im.common.constant.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Admin
 */
@Component
@Scope("prototype")
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 限制的超时时间
     */
    @Autowired
    ImConfigInfo imConfigInfo;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SessionSocketHolder sessionSocketHolder;


    /**
     * IdleStateHandler 发生超时事件是回调该方法
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case READER_IDLE:
                    handleAllIdle(ctx);
                    break;
                case WRITER_IDLE:
                    // 不处理
                    break;
                case ALL_IDLE:
                    break;
            }
        }
    }


    /**
     * 1、超时次数统计，保存在channel的属性中，由于当前的 HeartBeatServerHandler 是交给 Spring 容器管理的是单例对象，所以每个客户端连接都会共用同一个对象，所以需要将超时次数保存在 channel 的属性中，
     * 2、如果超时次数默认达到 3 次就会，断开连接
     *
     * @param ctx
     */
    private void handleAllIdle(ChannelHandlerContext ctx) {
        try {
            AttributeKey<Long> readTimeCountKey = AttributeKey.valueOf(Constants.READ_TIME_COUNT);
            Long idleCount = ctx.channel().attr(readTimeCountKey).get();
            if (idleCount == null) {
                ctx.channel().attr(readTimeCountKey).set(1L);
            } else {
                // 接收到心跳信息的是否会重置为 0 ，所以先自加
                ++idleCount;
                if (idleCount > imConfigInfo.getMaxReadTimeoutCount()) {
                    sessionSocketHolder.offlineUserSession((NioSocketChannel) ctx.channel());
                } else {
                    ctx.channel().attr(readTimeCountKey).set(idleCount);
                }
            }
        } catch (Exception e) {
            // 日志记录或其他异常处理逻辑
            e.printStackTrace();
        }
    }
}
