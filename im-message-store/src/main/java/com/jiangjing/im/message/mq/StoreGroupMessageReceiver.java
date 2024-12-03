package com.jiangjing.im.message.mq;

import com.alibaba.fastjson2.JSONObject;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.message.model.DoStoreGroupMessageDto;
import com.jiangjing.im.message.service.StoreMessageService;
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
 * 监听指定的 mq 队列，获取群聊消息，并持久化消息
 * @author scenery
 */
@Component
public class StoreGroupMessageReceiver {

    private static final Logger logger = LoggerFactory.getLogger(StoreGroupMessageReceiver.class);

    @Autowired
    StoreMessageService storeMessageService;


    @RabbitListener(
            bindings = @QueueBinding(value = @Queue(value = Constants.RabbitConstants.STORE_GROUP_MESSAGE, durable = "true"), exchange = @Exchange(value = Constants.RabbitConstants.STORE_GROUP_MESSAGE), key = Constants.RabbitConstants.STORE_GROUP_MESSAGE), concurrency = "10"
    )
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel) throws Exception {
        String messageStr = new String(message.getBody(), StandardCharsets.UTF_8);
        // 转成发送前封装的对象
        logger.info("CHAT MSG FORM QUEUE ::: {}", messageStr);
        try {
            // 是否支持复杂对象转换？ 需要调试确认
            DoStoreGroupMessageDto doStoreGroupMessageDto = JSONObject.parseObject(messageStr, DoStoreGroupMessageDto.class);
            storeMessageService.doStoreGroupMessage(doStoreGroupMessageDto);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            logger.error("处理消息出现异常：{}", e.getMessage());
            logger.error("RMQ_CHAT_TRAN_ERROR", e);
            logger.error("NACK_MSG:{}", messageStr);
            /*
             * param1:需要取人的消息id
             * param2:是否批量确认此id之前的所有消息
             * param3: true 消息重新发送，false 消息丢弃
             */
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
