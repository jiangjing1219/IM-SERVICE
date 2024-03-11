package com.example.imtcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.imtcp.config.ImConfigInfo;
import com.example.imtcp.rabbitmq.MqMessageProducer;
import com.example.imtcp.utils.SessionSocketHolder;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.dubbo.CheckMessageService;
import com.jiangjing.im.common.enums.ImConnectStatusEnum;
import com.jiangjing.im.common.enums.command.GroupEventCommand;
import com.jiangjing.im.common.enums.command.MessageCommand;
import com.jiangjing.im.common.enums.command.SystemCommand;
import com.jiangjing.im.common.enums.command.UserEventCommand;
import com.jiangjing.im.common.model.UserClientDto;
import com.jiangjing.im.common.model.UserSession;
import com.jiangjing.im.common.model.message.CheckSendMessageReq;
import com.jiangjing.pack.LoginPack;
import com.jiangjing.pack.message.ChatMessageAck;
import com.jiangjing.pack.user.LoginAckPack;
import com.jiangjing.pack.user.UserStatusChangeNotifyPack;
import com.jiangjing.proto.Message;
import com.jiangjing.proto.MessagePack;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * 解析 netty 信息
 *
 * @author jingjing
 * @date 2023/6/23 19:35
 */
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ImConfigInfo imConfigInfo;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MqMessageProducer messageProducer;

    @Autowired
    SessionSocketHolder sessionSocketHolder;

    /**
     * 远程调用 dubbo 服务
     */
    @DubboReference(version = "1.0.0.0", group = "im")
    CheckMessageService checkMessageService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) {
        Integer command = message.getMessageHeader().getCommand();
        // 登陆操作
        if (SystemCommand.LOGIN.getCommand() == command) {
            LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(message.getMessagePackage()), new TypeReference<LoginPack>() {
            }.getType());
            // 当前登陆的用户id
            String userId = loginPack.getUserId();

            // 设置 channel 的业务属性
            // 1、userid
            channelHandlerContext.channel().attr(AttributeKey.valueOf(Constants.USERID)).set(userId);
            // 2、clientType   imei
            String imeiInfo = message.getMessageHeader().getMessageType() + ":" + message.getMessageHeader().getImei();
            channelHandlerContext.channel().attr(AttributeKey.valueOf(Constants.CLIENT_IMEI)).set(imeiInfo);
            // 3、appid
            channelHandlerContext.channel().attr(AttributeKey.valueOf(Constants.APPID)).set(message.getMessageHeader().getAppId());
            // 4、clientType
            channelHandlerContext.channel().attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).set(message.getMessageHeader().getClientType());
            // 5、imei
            channelHandlerContext.channel().attr(AttributeKey.valueOf(Constants.IMEI)).set(message.getMessageHeader().getImei());

            // 将 channelHandlerContext 保存到本地缓存中
            // 因为当前用户存在多个客户端登录所以使用 Hash 类型存储，key 为 appid：userSession：userid   filed为：clientType  value为：session 对象
            UserSession userSession = new UserSession();
            userSession.setAppId(message.getMessageHeader().getAppId());
            userSession.setClientType(message.getMessageHeader().getClientType());
            userSession.setUserId(loginPack.getUserId());
            userSession.setConnectState(ImConnectStatusEnum.ONLINE_STATUS.getCode());
            userSession.setImei(message.getMessageHeader().getImei());
            // session 添加登录的服务的标识，在分布式中需要知道当前用户登录的是哪台服务器，因为 NioSocketChannel 是保存到在服务器本地的
            userSession.setBrokerId(imConfigInfo.getBrokerId());
            try {
                // 获取当前服务器的ip，标识登录的服务器
                InetAddress localHost = InetAddress.getLocalHost();
                // 放入的Session中
                userSession.setBrokerHost(localHost.getHostAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 构建 session 的 key  =  appid：userSession：userid   —— clientType:imei —— UserSession
            String key = message.getMessageHeader().getAppId() + Constants.RedisConstants.USER_SESSION_CONSTANTS + loginPack.getUserId();
            // 构建登录端的标识 _ 是否需要限制同一端多次登录？？
            String filed = message.getMessageHeader().getClientType() + ":" + message.getMessageHeader().getImei();
            // 将指定的session保存到redis中，本质就是构建用户登录的一个路由层，当前用户登录的是设备信息、登录状态、在哪台netty实例登录
            redisTemplate.opsForHash().put(key, filed, JSONObject.toJSONString(userSession));
            // 本地缓存 NioSocketChannel
            SessionSocketHolder.put(message.getMessageHeader().getAppId(), loginPack.getUserId(), message.getMessageHeader().getClientType(), message.getMessageHeader().getImei(), (NioSocketChannel) channelHandlerContext.channel());

            // 发布/订阅（需要支持对端登录，根据多端登录的类型，做相应的操作，需要广播发送当前的登录信息，由netty实例根据设计情况做相应的操作）
            // 实现的方式由很多种：1、参考redis的一致性Hash存储，使用userid做hash取余，指定只能登录的netty实例，弊端：扩容需要 reHash   2、通过路由层获取登录信息，再向执行的netty实例发送登出指定。弊端：不确定用户什么时候其他端就会下线，还要具体通知所属实例，繁琐。 3、广播，将登录信息广播个所有的netty实例，netty 实例根据自身服务器当前用户的其他登录信息做相应处理即可
            // 教程是使用 redisson 的 发布订阅 功能，感觉直接使用 RabbitMQ 的 foundout 模式更好,拓展新技能就直接使用 redisson
            UserClientDto dto = new UserClientDto();
            dto.setImei(message.getMessageHeader().getImei());
            dto.setUserId(loginPack.getUserId());
            dto.setClientType(message.getMessageHeader().getClientType());
            dto.setAppId(message.getMessageHeader().getAppId());
            rabbitTemplate.convertAndSend("user_login_broadcast", "", JSONObject.toJSONString(dto));

            // 用户在线状态
            UserStatusChangeNotifyPack userStatusChangeNotifyPack = new UserStatusChangeNotifyPack();
            userStatusChangeNotifyPack.setUserId(userId);
            // 在线状态
            userStatusChangeNotifyPack.setStatus(ImConnectStatusEnum.ONLINE_STATUS.getCode());
            userStatusChangeNotifyPack.setAppId(message.getMessageHeader().getAppId());
            // 投递给 mq ，service 层处理
            messageProducer.sendMessage(userStatusChangeNotifyPack, UserEventCommand.USER_ONLINE_STATUS_CHANGE.getCommand(), message.getMessageHeader());

            // 登录成功，回复给客户局端
            MessagePack<LoginAckPack> loginSuccessAckPack = new MessagePack<>();
            LoginAckPack pack = new LoginAckPack();
            pack.setUserId(loginPack.getUserId());
            loginSuccessAckPack.setData(pack);
            loginSuccessAckPack.setCommand(SystemCommand.LOGINACK.getCommand());
            loginSuccessAckPack.setImei(message.getMessageHeader().getImei());
            loginSuccessAckPack.setAppId(message.getMessageHeader().getAppId());
            loginSuccessAckPack.setClientType(message.getMessageHeader().getClientType());
            channelHandlerContext.channel().writeAndFlush(loginSuccessAckPack);

        } else if (command == SystemCommand.LOGOUT.getCommand()) {
            //删除session —— 离线状态变更
            sessionSocketHolder.removeUserSession((NioSocketChannel) channelHandlerContext.channel());
            // 心跳事件
        } else if (command == SystemCommand.PING.getCommand()) {
            // 写入当前心跳的时间
            channelHandlerContext.channel()
                    .attr(AttributeKey.valueOf(Constants.READ_TIME)).set(System.currentTimeMillis());
            // 如果发送的是单聊消息或者是群聊消息，那么直接在这里校验发送方和接收方的合法性，不合法直接返回 ack ，不需要投递到 mq
        } else if (command == MessageCommand.MSG_P2P.getCommand()
                || command == GroupEventCommand.MSG_GROUP.getCommand()) {
            CheckSendMessageReq checkSendMessageReq = new CheckSendMessageReq();
            checkSendMessageReq.setAppId(message.getMessageHeader().getAppId());
            checkSendMessageReq.setCommand(command);
            JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(message.getMessagePackage()));
            if (command == MessageCommand.MSG_P2P.getCommand()) {
                checkSendMessageReq.setToId(jsonObject.getString("toId"));
            } else {
                checkSendMessageReq.setGroupId(jsonObject.getString("groupId"));
            }
            checkSendMessageReq.setFromId(jsonObject.getString("fromId"));
            // dubbo 远程调用，消息发送合法性校验
            ResponseVO responseVO = checkMessageService.checkSendMessage(checkSendMessageReq);
            if (responseVO.isOk()) {
                messageProducer.sendMessage(message, command);
            } else {
                // 直接回复失败 ack
                int ackCommand = 0;
                if (command == MessageCommand.MSG_P2P.getCommand()) {
                    ackCommand = MessageCommand.MSG_ACK.getCommand();
                } else {
                    ackCommand = GroupEventCommand.GROUP_MSG_ACK.getCommand();
                }
                ChatMessageAck chatMessageAck = new ChatMessageAck(jsonObject.getString("messageId"));
                responseVO.setData(chatMessageAck);
                MessagePack<ResponseVO> ack = new MessagePack<>();
                ack.setData(responseVO);
                ack.setCommand(ackCommand);
                ack.setAppId(message.getMessageHeader().getAppId());
                ack.setMessageId(jsonObject.getString("messageId"));
                // 直接包装成  MessagePack ，回复给客户端
                channelHandlerContext.channel().writeAndFlush(ack);
            }
        } else {
            // 同步到业务服务器
            messageProducer.sendMessage(message, command);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //设置离线
        SessionSocketHolder.remove((NioSocketChannel) ctx.channel());
        // 修改 redis中的在线状态
        // 更改 Redis 中用户的状态
        String userId = (String) ctx.channel().attr(AttributeKey.valueOf(Constants.USERID)).get();
        Integer appId = (Integer) ctx.channel().attr(AttributeKey.valueOf(Constants.APPID)).get();
        Integer clientType = (Integer) ctx.channel().attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
        String imei = (String) ctx.channel()
                .attr(AttributeKey.valueOf(Constants.IMEI)).get();
        String strSession = (String) redisTemplate.opsForHash().get(appId +
                Constants.RedisConstants.USER_SESSION_CONSTANTS + userId, clientType + ":" + imei);
        // 变更redis中的该客户端的在线状态
        if (StringUtils.isNotBlank(strSession)) {
            UserSession userSession = JSONObject.parseObject(strSession, UserSession.class);
            userSession.setConnectState(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            redisTemplate.opsForHash().put(appId +
                    Constants.RedisConstants.USER_SESSION_CONSTANTS + userId, clientType + ":" + imei, JSONObject.toJSONString(userSession));
        }
        ctx.close();
    }
}
