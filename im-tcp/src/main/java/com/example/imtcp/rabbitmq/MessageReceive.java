package com.example.imtcp.rabbitmq;


import com.alibaba.fastjson.JSONObject;
import com.example.imtcp.config.ImConfigInfo;
import com.example.imtcp.rabbitmq.process.ProcessFactory;
import com.example.imtcp.utils.SessionSocketHolder;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.ClientType;
import com.jiangjing.im.common.enums.DeviceMultiLoginEnum;
import com.jiangjing.im.common.enums.command.SystemCommand;
import com.jiangjing.im.common.model.UserClientDto;
import com.jiangjing.proto.MessagePack;
import com.rabbitmq.client.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author admin
 * <p>
 * 消费端相关的配置
 * 客户端每次获取的消息数（前提是开始手动确认模式）：spring.rabbitmq.listener.simple.prefetch=1
 * 开启手动确认模式：spring.rabbitmq.listener.simple.acknowledge-mode=manual
 */
@Component
public class MessageReceive {

    @Autowired
    private ImConfigInfo imConfigInfo;

    private final static Logger logger = LoggerFactory.getLogger(MessageReceive.class);

    /**
     * 这里是业务系统（MessageService --> TCP （IM） tcp 回调的（用户登录端之间的信息同步））
     * 声明需要监听的队列 —— brokerId 当前服务器的唯一标识
     * 消费者端，
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = Constants.RabbitConstants.MESSAGE_SERVICE_2_IM + "_" + "#{imConfigInfo.brokerId}")
    public void publishSubscribeListener(Message message, Channel channel) throws IOException {

        try {
            String messageStr = new String(message.getBody());
            logger.info(messageStr);
            MessagePack messagePack = com.alibaba.fastjson2.JSONObject.parseObject(messageStr, MessagePack.class);
            ProcessFactory.getMessageProcess(messagePack.getCommand()).process(messagePack);
            /**
             * param1:需要取人的消息id
             * param2:是否批量确认此id之前的所有消息
             * param3: true 消息重新发送，false 消息丢弃
             */
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    /**
     * '
     * 登录完成时，广播登录消息，实现多端登录的逻辑
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = "user_login_queue")
    public void userLoginListener(Message message, Channel channel) throws IOException {

        logger.info("user_login_queue_接收到消息：" + new String(message.getBody()));
        System.out.println("处理相关业务方法-fanout_queue");

        String msg = new String(message.getBody());
        UserClientDto dto = JSONObject.parseObject(msg, UserClientDto.class);
        /**
         * 获取当前登录用户的所有的登录信息
         */
        List<NioSocketChannel> nioSocketChannels = SessionSocketHolder.get(dto.getAppId(), dto.getUserId());
        Integer loginModel = imConfigInfo.getLoginModel();
        for (NioSocketChannel nioSocketChannel : nioSocketChannels) {
            Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
            String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.IMEI)).get();
            if (loginModel == DeviceMultiLoginEnum.ONE.getLoginMode()) {
                // 单端登录，下线通知(不能简单地踢下线，防止消息丢失)

                // 当前登录是否就是本机，不是则需需要发送下线通知，服务端发送给客户端
                if (!(clientType + ":" + imei).equals(dto.getClientType() + ":" + dto.getImei())) {
                    MessagePack<Object> pack = new MessagePack<>();
                    pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.USERID)).get());
                    pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.USERID)).get());
                    pack.setCommand(SystemCommand.MUTUALLOGIN.getCommand());
                    nioSocketChannel.writeAndFlush(pack);
                }

                // 双端登录，允许 web 端 和  电脑/移动
            } else if (loginModel == DeviceMultiLoginEnum.TWO.getLoginMode()) {

                // 当前登录是 web 端登录
                if (dto.getClientType() == ClientType.WEB.getCode()) {
                    continue;
                }
                // 本地登录的是web端
                if (clientType == ClientType.WEB.getCode()) {
                    continue;
                }
                // 当前的是 移动端/pc端   ，只能能存在一个登录设备，所以只要遍历的channel不是当前登录的channel就需要发送下线通知
                if (!(clientType + ":" + imei).equals(dto.getClientType() + ":" + dto.getImei())) {
                    MessagePack<Object> pack = new MessagePack<>();
                    pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.USERID)).get());
                    pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.USERID)).get());
                    pack.setCommand(SystemCommand.MUTUALLOGIN.getCommand());
                    nioSocketChannel.writeAndFlush(pack);
                }

                // 三端登录（web、移动、pc）
            } else if (loginModel == DeviceMultiLoginEnum.THREE.getLoginMode()) {
                if (dto.getClientType() == ClientType.WEB.getCode()) {
                    continue;
                }

                // 移动端判断    android  / ios 判断
                boolean isSameClient = (clientType == ClientType.IOS.getCode() || clientType == ClientType.ANDROID.getCode()) &&
                        (dto.getClientType() == ClientType.IOS.getCode() || dto.getClientType() == ClientType.ANDROID.getCode());

                // pc端判断  mac / windows 判断
                if ((clientType == ClientType.MAC.getCode() || clientType == ClientType.WINDOWS.getCode()) &&
                        (dto.getClientType() == ClientType.MAC.getCode() || dto.getClientType() == ClientType.WINDOWS.getCode())) {
                    isSameClient = true;
                }

                // 当前登录和遍历的Channel 的 ClientType 冲突了，需要判断是否是本机登录，不是就需要下线
                if (isSameClient && !(clientType + ":" + imei).equals(dto.getClientType() + ":" + dto.getImei())) {
                    MessagePack<Object> pack = new MessagePack<>();
                    pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.USERID)).get());
                    pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.USERID)).get());
                    pack.setCommand(SystemCommand.MUTUALLOGIN.getCommand());
                    nioSocketChannel.writeAndFlush(pack);
                }
            }
        }
        /**
         * param1:需要取人的消息id
         * param2:是否批量确认此id之前的所有消息
         * param3: true 消息重新发送，false 消息丢弃
         */
        //channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
