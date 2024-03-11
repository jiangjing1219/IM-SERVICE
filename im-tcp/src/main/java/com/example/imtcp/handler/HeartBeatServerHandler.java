package com.example.imtcp.handler;

import com.example.imtcp.config.ImConfigInfo;
import com.example.imtcp.utils.SessionSocketHolder;
import com.jiangjing.im.common.constant.Constants;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Admin
 */
@Component
@ChannelHandler.Sharable
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
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        String eventType = null;
        switch (event.state()) {
            case READER_IDLE:
                eventType = "读空闲";
                break;
            case WRITER_IDLE:
                eventType = "写空闲";
                // 不处理
                break;
            case ALL_IDLE:
                eventType = "读写空闲";
                // 发生全超时事件，判断需要关闭链接，获取channel上的最后一次读数据的时间属性（发生读事件时写入）
                Long lastReadTime = (Long) ctx.channel().attr(AttributeKey.valueOf(Constants.READ_TIME)).get();
                if (System.currentTimeMillis() - lastReadTime > imConfigInfo.getHeartBeatTime()) {
                    sessionSocketHolder.offlineUserSession((NioSocketChannel) ctx.channel());
                }
                break;
        }
    }
}
