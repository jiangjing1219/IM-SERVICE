package com.jiangjing.im.service.utils;

import com.alibaba.fastjson.JSONObject;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.command.Command;
import com.jiangjing.im.common.model.ClientInfo;
import com.jiangjing.im.common.model.UserSession;
import com.jiangjing.proto.MessagePack;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 单个用户的消息发送
 *
 * @author Admin
 */
@Component
public class MessageProducer {


    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    UserSessionUtils userSessionUtils;


    /**
     * 用户端发送消息给指定用户（MessageService  业务端发送消息给   TCP 服务器   -》 NioServerSocketChannel  -》 用户客户端）
     * 主要的作用的是 tpc回调，多端信息同步
     * <p>
     * MessageService2Im ： 指定的交换机
     * BrokerId ： 目标用户登录的服务器id
     *
     * @param session
     * @param msg
     * @return
     */
    public boolean sendMessage(UserSession session, Object msg) {
        try {
            logger.info("send message == " + msg);
            String exchangeName = Constants.RabbitConstants.MESSAGE_SERVICE_2_IM;
            rabbitTemplate.convertAndSend(exchangeName, String.valueOf(session.getBrokerId()), msg);
            return true;
        } catch (Exception e) {
            logger.error("send error :" + e.getMessage());
            return false;
        }
    }


    /**
     * 包装成 数据包
     *
     * @param toId
     * @param command
     * @param msg
     * @param session
     * @return
     */
    public boolean sendPack(String toId, Command command, Object msg, UserSession session) {
        MessagePack messagePack = new MessagePack();
        messagePack.setToId(toId);
        messagePack.setCommand(command.getCommand());
        messagePack.setClientType(session.getClientType());
        messagePack.setImei(session.getImei());
        messagePack.setUserId(session.getUserId());
        messagePack.setAppId(session.getAppId());
        messagePack.setImei(session.getImei());
        // 将消息转成 JSONObject
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(msg));
        messagePack.setData(jsonObject);
        // 把message对象转成 String
        String body = JSONObject.toJSONString(messagePack);
        return sendMessage(session, body);
    }

    /**
     * 发送给指定用户的所有端
     *
     * @param toId    目标用户的 userid
     * @param appId   app
     * @param command 命令类型
     * @param data    消息内容
     * @return 返回奉送成功的登录端信息
     */
    public List<ClientInfo> sendToUserByAll(String toId, Integer appId, Command command, Object data) {
        // 获取当前客户的所有的登录的端信息(在线状态)
        List<UserSession> sessions = userSessionUtils.getUserSession(appId, toId);
        ArrayList<ClientInfo> list = new ArrayList<>();
        sessions.forEach(session -> {
            boolean result = sendPack(toId, command, data, session);
            if (result) {
                ClientInfo clientInfo = new ClientInfo(appId, session.getClientType(), session.getImei());
                list.add(clientInfo);
            }
        });
        return list;
    }

    /**
     * 发送给指定的用户的某一客户端
     *
     * @param toId
     * @param command
     * @param data
     * @param clientInfo
     */
    public boolean sendToUserByOne(String toId, Command command, Object data, ClientInfo clientInfo) {
        UserSession userSession = userSessionUtils.getUserSession(clientInfo.getAppId(), toId, clientInfo.getClientType(), clientInfo.getImei());
        if (userSession != null) {
            return sendPack(toId, command, data, userSession);
        } else {
            return false;
        }
    }

    /**
     * 判断是否是管理员发送  条件
     *
     * @param toId
     * @param appId
     * @param clientType
     * @param imei
     * @param command
     * @param data
     */
    public void sendToUserByConditions(String toId, Integer appId, Integer clientType, String imei, Command command, Object data) {
        // 如果 clientType、imei 都不为空，说明时自身发送的消息，需要排除当前端，其他端去同步数据
        if (clientType != null && StringUtils.isNotBlank(imei)) {
            ClientInfo clientInfo = new ClientInfo(appId, clientType, imei);
            sendToUserExceptClient(toId, command, data, clientInfo);
        } else {
            // 管理员发送，所有端都要同步数据
            sendToUserByAll(toId, appId, command, data);
        }
    }

    /**
     * 发送给排除某一端之外的其他端
     *
     * @param toId
     * @param command
     * @param data
     * @param clientInfo
     */
    public void sendToUserExceptClient(String toId, Command command, Object data, ClientInfo clientInfo) {
        List<UserSession> sessions = userSessionUtils.getUserSession(clientInfo.getAppId(), toId);
        sessions.forEach(userSession -> {
            if (!isMatch(userSession, clientInfo)) {
                sendPack(toId, command, data, userSession);
            }
        });

    }


    /**
     * 判断是否时相同的端
     * <p>
     * 只有 appid、client、imei 同时都相等时，才会认为是相同端
     * <p>
     * 不可能存在  appid、client 相同，但是 imei 同步的情况发生，因为在登录时已经对登录端继续进行了限制，同一端只允许一台设备登录
     *
     * @param sessionDto
     * @param clientInfo
     * @return
     */
    private boolean isMatch(UserSession sessionDto, ClientInfo clientInfo) {
        return Objects.equals(sessionDto.getAppId(), clientInfo.getAppId())
                && Objects.equals(sessionDto.getImei(), clientInfo.getImei())
                && Objects.equals(sessionDto.getClientType(), clientInfo.getClientType());
    }
}
