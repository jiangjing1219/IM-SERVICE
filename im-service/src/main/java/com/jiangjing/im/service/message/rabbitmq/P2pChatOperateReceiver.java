package com.jiangjing.im.service.message.rabbitmq;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.command.MessageCommand;
import com.jiangjing.im.common.model.message.MessageContent;
import com.jiangjing.im.common.model.message.MessageReadedContent;
import com.jiangjing.im.common.model.message.MessageReceiveAckContent;
import com.jiangjing.im.common.model.message.RecallMessageContent;
import com.jiangjing.im.service.message.service.MessageSyncService;
import com.jiangjing.im.service.message.service.P2PMessageService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 接收 TCP 服务器发送过来的消息，主要是聊天消息备份（ImToMessageService）
 *
 * @author
 */
@Component
public class P2pChatOperateReceiver {
    private static final Logger logger = LoggerFactory.getLogger(P2pChatOperateReceiver.class);

    @Autowired
    P2PMessageService p2PMessageService;

    @Autowired
    MessageSyncService messageSyncService;

    /**
     * 默认的交换机类型是 direct ，根据路由key精确匹配。
     * TCP 发送的给 MessageService，所有的 MessageService 服务平分队列中的消息
     *
     * 业务场景：如果第三方的 app 需要在发送消息之前或者发送消息之后需要做自身的业务判断来限制当前用户发消息的合法性，就需要在发消息之前做业务回调
     *
     * @param message
     * @param headers
     * @param channel
     */
    @RabbitListener(
            bindings = @QueueBinding(value = @Queue(value = Constants.RabbitConstants.IM_2_MESSAGE_SERVICE, durable = "true"), exchange = @Exchange(value = Constants.RabbitConstants.IM_2_MESSAGE_SERVICE), key = Constants.RabbitConstants.IM_2_MESSAGE_SERVICE), concurrency = "1"
    )
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        logger.info("CHAT MSG FORM QUEUE ::: {}", msg);
        try {
            JSONObject messageJson = JSON.parseObject(msg);
            // 获取消息的指令类型，单聊/群聊
            Integer command = messageJson.getInteger("command");
            // 单聊消息
            if (command == MessageCommand.MSG_P2P.getCommand()) {
                // 处理消息
                MessageContent messageContent = messageJson.toJavaObject(MessageContent.class);
                p2PMessageService.process(messageContent);
                // 接收端接收到消息回复的 ack 消息
            } else if (command == MessageCommand.MSG_RECIVE_ACK.getCommand()) {
                // 解析 ack 确认包的信息
                MessageReceiveAckContent receiveAckContent = messageJson.toJavaObject(MessageReceiveAckContent.class);
                messageSyncService.receiveMark(receiveAckContent);
            } else if (command == MessageCommand.MSG_READED.getCommand()) {
                // 接收方读取到消息之后，回复 MSG_READED
                MessageReadedContent messageContent = messageJson.toJavaObject(MessageReadedContent.class);
                messageSyncService.readMark(messageContent);
            }else if (command == MessageCommand.MSG_RECALL.getCommand()) {
                // 接收到单聊消息的撤回指令
                RecallMessageContent messageContent = messageJson.toJavaObject(RecallMessageContent.class);
                messageSyncService.recallMessage(messageContent);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            logger.error("处理消息出现异常：{}", e.getMessage());
            logger.error("RMQ_CHAT_TRAN_ERROR", e);
            logger.error("NACK_MSG:{}", msg);
            /*
             * param1:需要取人的消息id
             * param2:是否批量确认此id之前的所有消息
             * param3: true 消息重新发送，false 消息丢弃
             */
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }

    }

}
