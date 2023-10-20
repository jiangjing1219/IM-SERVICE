package com.example.imtcp.rabbitmq;

import com.alibaba.fastjson2.JSONObject;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.command.CommandType;
import com.jiangjing.proto.Message;
import com.jiangjing.proto.MessageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 将聊天消息发送给业务服务器（ImToMessageService）
 *
 * @author admin
 */
@Component
public class MqMessageProducer {
    private final static Logger logger = LoggerFactory.getLogger(MqMessageProducer.class);


    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 接收到聊天消息，同步给业务服务
     *
     * @param message 自定义协力的 Message
     * @param command 命令类型
     */
    public void sendMessage(Message message, Integer command) {
        // 默认是单聊消息
        // 判断手否是群组服务的消息，命令义 2 开头的都是群组服务的消息,1 开头的是单聊消息
        String commandSub = command.toString().substring(0, 1);
        CommandType commandType = CommandType.getCommandType(commandSub);
        String exchangeName = "";
        if(commandType == CommandType.MESSAGE){
            exchangeName = Constants.RabbitConstants.IM_2_MESSAGE_SERVICE;
        }else if(commandType == CommandType.GROUP){
            exchangeName = Constants.RabbitConstants.IM_2_GROUP_SERVICE;
        }else if(commandType == CommandType.FRIEND){
            exchangeName = Constants.RabbitConstants.IM_2_FRIENDSHIP_SERVICE;
        }else if(commandType == CommandType.USER){
            exchangeName = Constants.RabbitConstants.IM_2_USER_SERVICE;
        }
        try {
            // 只发送 messagePackage ，提出的 clientInfo
            JSONObject packageObj = JSONObject.from(message.getMessagePackage());
            packageObj.put("command", command);
            packageObj.put("clientType", message.getMessageHeader().getClientType());
            packageObj.put("imei", message.getMessageHeader().getImei());
            packageObj.put("appId", message.getMessageHeader().getAppId());
            // 使用的 redirect 交换机，精确匹配，routingKey 和交换机名称一致（需要测试）
            rabbitTemplate.convertAndSend(exchangeName, exchangeName, packageObj.toJSONString().getBytes(StandardCharsets.UTF_8));
        } catch (AmqpException e) {
            logger.error("发送消息出现异常：{}", e.getMessage());
        }
    }

    public void sendMessage(Object message, Integer command, MessageHeader messageHeader) {
        // 默认是单聊消息
        // 判断手否是群组服务的消息，命令义 2 开头的都是群组服务的消息,1 开头的是单聊消息
        String commandSub = command.toString().substring(0, 1);
        CommandType commandType = CommandType.getCommandType(commandSub);
        String exchangeName = "";
        if(commandType == CommandType.MESSAGE){
            exchangeName = Constants.RabbitConstants.IM_2_MESSAGE_SERVICE;
        }else if(commandType == CommandType.GROUP){
            exchangeName = Constants.RabbitConstants.IM_2_GROUP_SERVICE;
        }else if(commandType == CommandType.FRIEND){
            exchangeName = Constants.RabbitConstants.IM_2_FRIENDSHIP_SERVICE;
        }else if(commandType == CommandType.USER){
            exchangeName = Constants.RabbitConstants.IM_2_USER_SERVICE;
        }
        try {
            JSONObject packageObj = JSONObject.from(message);
            packageObj.put("command", command);
            packageObj.put("clientType", messageHeader.getClientType());
            packageObj.put("imei", messageHeader.getImei());
            packageObj.put("appId", messageHeader.getAppId());
            // 使用的 redirect 交换机，精确匹配，routingKey 和交换机名称一致（需要测试）
            rabbitTemplate.convertAndSend(exchangeName, exchangeName, packageObj.toJSONString().getBytes(StandardCharsets.UTF_8));
        } catch (AmqpException e) {
            logger.error("发送消息出现异常：{}", e.getMessage());
        }
    }
}
