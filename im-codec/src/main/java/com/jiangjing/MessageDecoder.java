package com.jiangjing;

import com.alibaba.fastjson.JSONObject;
import com.jiangjing.proto.Message;
import com.jiangjing.proto.MessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * netty 自定义协议的解码器
 *
 * 这里定义的 MessageDecoder 和 MessageEncoder 并不是配对的。这里的客户端其实就是 app，NettyServer 发送消息进行 MessageEncoder ，那么对应的解码端也是在 App 端继续解码，只 App 端的解码规则匹配即可。所谓的 MessageDecoder 解码，也是在客户端按照解码的规则要求发送消息即可。
 *
 * @author jingjing
 * @date 2023/6/23 17:42
 */
public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int readableBytes = byteBuf.readableBytes();
        // 请求头中以四个字节表示一个要素，共 7 个要素。
        if (readableBytes < 28) {
            return;
        }
        //命令
        int common = byteBuf.readInt();
        // 版本号
        int version = byteBuf.readInt();

        int clientType = byteBuf.readInt();

        int appId = byteBuf.readInt();

        int messageType = byteBuf.readInt();

        int imeiLength = byteBuf.readInt();

        int length = byteBuf.readInt();

        if (byteBuf.readableBytes() < imeiLength + length) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] imeiByte = new byte[imeiLength];
        byteBuf.readBytes(imeiByte);

        byte[] contentByte = new byte[length];
        byteBuf.readBytes(contentByte);

        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setCommand(common);
        messageHeader.setVersion(version);
        messageHeader.setClientType(clientType);
        messageHeader.setAppId(appId);
        messageHeader.setMessageType(messageType);
        messageHeader.setImei(new String(imeiByte));

        Message message = new Message();
        message.setMessageHeader(messageHeader);
        // json 格式
        if (messageType == 0x0) {
            String body = new String(contentByte);
            JSONObject parse = (JSONObject) JSONObject.parse(body);
            message.setMessagePackage(parse);
        }
        list.add(message);
    }
}
