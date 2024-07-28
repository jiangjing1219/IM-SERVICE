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
     * <p>
     * 业务场景：如果第三方的 app 需要在发送消息之前或者发送消息之后需要做自身的业务判断来限制当前用户发消息的合法性，就需要在发消息之前做业务回调
     *
     * @param message
     * @param headers
     * @param channel
     */
    @RabbitListener(
            bindings = @QueueBinding(value = @Queue(value = Constants.RabbitConstants.IM_2_MESSAGE_SERVICE, durable = "true"), exchange = @Exchange(value = Constants.RabbitConstants.IM_2_MESSAGE_SERVICE), key = Constants.RabbitConstants.IM_2_MESSAGE_SERVICE), concurrency = "10"
    )
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        logger.info("CHAT MSG FORM QUEUE ::: {}", msg);
        try {
            JSONObject messageJson = JSON.parseObject(msg);
            Integer command = messageJson.getInteger("command");
            processMessage(command, messageJson);

            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
                logger.error("Ack failed, message will be retried: {}", msg, e);
                try {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                } catch (IOException nackEx) {
                    logger.error("Nack failed: {}", nackEx.getMessage(), nackEx);
                }
            }
        } catch (Exception e) {
            logger.error("Processing message failed: {}", msg, e);
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException nackEx) {
                logger.error("Nack failed: {}", nackEx.getMessage(), nackEx);
            }
        }
    }

    private void processMessage(Integer command, JSONObject messageJson) {
        if (command == MessageCommand.MSG_P2P.getCommand()) {
            MessageContent messageContent = messageJson.toJavaObject(MessageContent.class);
            p2PMessageService.process(messageContent);
        } else if (command == MessageCommand.MSG_RECEIVE_ACK.getCommand()) {
            MessageReceiveAckContent receiveAckContent = messageJson.toJavaObject(MessageReceiveAckContent.class);
            messageSyncService.receiveMark(receiveAckContent);
        } else if (command == MessageCommand.MSG_READED.getCommand()) {
            MessageReadedContent messageContent = messageJson.toJavaObject(MessageReadedContent.class);
            messageSyncService.readMark(messageContent);
        } else if (command == MessageCommand.MSG_RECALL.getCommand()) {
            RecallMessageContent messageContent = messageJson.toJavaObject(RecallMessageContent.class);
            messageSyncService.recallMessage(messageContent);
        }
    }
}
