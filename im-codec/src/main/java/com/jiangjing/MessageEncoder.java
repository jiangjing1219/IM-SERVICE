package com.jiangjing;

import com.alibaba.fastjson.JSONObject;
import com.jiangjing.proto.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author: Chackylee
 * @description: 消息编码类，私有协议规则，前4位表示长度，接着command4位，后面是数据
 **/
public class MessageEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(msg instanceof MessagePack){
            MessagePack msgBody = (MessagePack) msg;
            String s = JSONObject.toJSONString(msgBody.getData());
            byte[] bytes = s.getBytes();
            // 指令类型
            out.writeInt(msgBody.getCommand());
            // 内容长度
            out.writeInt(bytes.length);
            // 消息内容
            out.writeBytes(bytes);
        }
    }

}
