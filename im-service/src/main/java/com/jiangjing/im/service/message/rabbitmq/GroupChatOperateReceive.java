package com.jiangjing.im.service.message.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.command.GroupEventCommand;
import com.jiangjing.im.common.model.message.GroupChatMessageContent;
import com.jiangjing.im.common.model.message.MessageReadedContent;
import com.jiangjing.im.service.message.service.GroupMessageService;
import com.jiangjing.im.service.message.service.MessageSyncService;
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
 *
 *
 * @author Admin
 */
@Component
public class GroupChatOperateReceive {
    private static final Logger logger = LoggerFactory.getLogger(P2pChatOperateReceiver.class);

    @Autowired
    GroupMessageService groupMessageService;

    @Autowired
    MessageSyncService messageSyncService;

    /**
     * 监听 Im2GroupService groupService 群聊消息的队列
     *
     * todo 后续优化可以使用 command 作为 routing Key，各自只获取对应的命令消息
     *
     * @param message
     * @param headers
     * @param channel
     * @throws IOException
     */
    @RabbitListener(
            bindings = @QueueBinding(value = @Queue(value = Constants.RabbitConstants.IM_2_GROUP_SERVICE, durable = "true"), exchange = @Exchange(value = Constants.RabbitConstants.IM_2_GROUP_SERVICE), key = Constants.RabbitConstants.IM_2_GROUP_SERVICE), concurrency = "1"
    )
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        logger.info("CHAT MSG FORM QUEUE ::: {}", msg);
        try {
            JSONObject jsonObject = JSON.parseObject(msg);
            Integer command = jsonObject.getInteger("command");
            // 处理群聊消息
            if (command == GroupEventCommand.MSG_GROUP.getCommand()) {
                GroupChatMessageContent messageContent = jsonObject.toJavaObject(GroupChatMessageContent.class);
                groupMessageService.process(messageContent);
                // 群聊消息已读（接收端回发的已读标识）
            } else if (command == GroupEventCommand.MSG_GROUP_READED.getCommand()) {
                // 解析消息内容
                MessageReadedContent messageContent = jsonObject.toJavaObject(MessageReadedContent.class);
                // 处理已读标识
                messageSyncService.groupReadMark(messageContent);
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
