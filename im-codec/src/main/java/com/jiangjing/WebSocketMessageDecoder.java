package com.jiangjing;

import com.jiangjing.proto.Message;
import com.jiangjing.utils.ByteBufToMessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * 自定义消息协议的解码器，需要判断所有的 7 执行符和执行的消息长度接收完成
 *
 * @author jiangjing
 */
public class WebSocketMessageDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) {

        ByteBuf content = msg.content();
        if (content.readableBytes() < 28) {
            return;
        }
        Message message = ByteBufToMessageUtils.transition(content);
        if (message == null) {
            return;
        }
        out.add(message);
    }
}
