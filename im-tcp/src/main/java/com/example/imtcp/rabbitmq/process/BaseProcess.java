package com.example.imtcp.rabbitmq.process;

import com.example.imtcp.utils.SessionSocketHolder;
import com.jiangjing.proto.MessagePack;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 一个基础消息接收处理类，如果需要扩展只需要继承该类，实现 before after 方法进行扩展即可
 *
 * @author Admin
 */
public abstract class BaseProcess {

    protected abstract void processBefore();

    /**
     * 发送端发送的时候已经包装成 MessagePack 对象,接收的消息都是当前服务的对应的 brokerId 消息，接收的用户登录在当前服务器上
     *
     * @param messagePack
     */
    public void process(MessagePack messagePack) {
        processBefore();
        // 获取接收用户的 NioSocketChannel
        NioSocketChannel nioSocketChannel = SessionSocketHolder.get(messagePack.getAppId(), messagePack.getToId(), messagePack.getClientType(), messagePack.getImei());
        // 直接发送消息
        if (nioSocketChannel != null) {
            nioSocketChannel.writeAndFlush(messagePack);
        }
        processAfter();
    }

    protected abstract void processAfter();
}
