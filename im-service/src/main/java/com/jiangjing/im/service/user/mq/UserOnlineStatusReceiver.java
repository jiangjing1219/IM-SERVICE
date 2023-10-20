package com.jiangjing.im.service.user.mq;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.command.UserEventCommand;
import com.jiangjing.im.service.user.model.UserStatusChangeNotifyContent;
import com.jiangjing.im.service.user.service.ImUserStatusService;
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
 * 用户在线状态通知（TCP 层投递消息）
 *
 * @author
 */
@Component
public class UserOnlineStatusReceiver {

    private static final Logger logger = LoggerFactory.getLogger(UserOnlineStatusReceiver.class);

    @Autowired
    ImUserStatusService imUserStatusService;


    /**
     * 监听 IM_2_USER_SERVICE  队列，默认是 Direct Exchange
     *
     * @param message
     * @param headers
     * @param channel
     * @throws IOException
     */
    @RabbitListener(
            bindings = @QueueBinding(value = @Queue(value = Constants.RabbitConstants.IM_2_USER_SERVICE, durable = "true"), exchange = @Exchange(value = Constants.RabbitConstants.IM_2_USER_SERVICE), key = Constants.RabbitConstants.IM_2_USER_SERVICE), concurrency = "1"
    )
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        logger.info("CHAT MSG FORM QUEUE ::: {}", msg);
        try {
            JSONObject jsonObject = JSON.parseObject(msg);
            Integer command = jsonObject.getInteger("command");
            // 在线状态变更的消息
            if (command == UserEventCommand.USER_ONLINE_STATUS_CHANGE.getCommand()) {
                UserStatusChangeNotifyContent content = JSON.parseObject(msg, new TypeReference<UserStatusChangeNotifyContent>() {
                }.getType());
                imUserStatusService.processUserOnlineStatusNotify(content);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
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
