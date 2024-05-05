package com.jiangjing;


import com.alibaba.fastjson.JSONObject;
import com.jiangjing.proto.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * web 端的编码，只需要发送 命令、消息长度、消息体 即可，默认是 json 字符串，用于前端解析
 *
 * @author Admin
 */
public class WebSocketMessageEncoder extends MessageToMessageEncoder<MessagePack> {

    private static final Logger log = LoggerFactory.getLogger(WebSocketMessageEncoder.class);
    private static final int HEADER_SIZE = 8; // 定义常量以避免硬编码

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePack msg, List<Object> out) {
        try {
            String jsonMessage = JSONObject.toJSONString(msg);
            ByteBuf byteBuf = allocateBuffer(jsonMessage.length());
            try {
                byte[] bytes = jsonMessage.getBytes();
                byteBuf.writeInt(msg.getCommand());
                byteBuf.writeInt(bytes.length);
                byteBuf.writeBytes(bytes);
                out.add(new BinaryWebSocketFrame(byteBuf));
            } catch (Exception e) {
                log.error("Failed to encode WebSocket message: {}", msg, e);
            }
        } catch (Exception e) {
            log.error("Unexpected error during WebSocket message encoding", e);
        }
    }

    /**
     * 用于分配ByteBuf的辅助方法，考虑了异常情况下的资源释放。
     *
     * @param contentLength 内容长度
     * @return 分配的ByteBuf
     */
    private ByteBuf allocateBuffer(int contentLength) {
        return Unpooled.directBuffer(HEADER_SIZE + contentLength);
    }
}
