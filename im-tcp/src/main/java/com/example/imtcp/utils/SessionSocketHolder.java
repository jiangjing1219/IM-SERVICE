package com.example.imtcp.utils;

import com.alibaba.fastjson.JSONObject;
import com.example.imtcp.rabbitmq.MqMessageProducer;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.ImConnectStatusEnum;
import com.jiangjing.im.common.enums.command.UserEventCommand;
import com.jiangjing.im.common.model.UserClientDto;
import com.jiangjing.im.common.model.UserSession;
import com.jiangjing.pack.user.UserStatusChangeNotifyPack;
import com.jiangjing.proto.MessageHeader;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jingjing
 * @date 2023/6/24 10:04
 */
@Component
public class SessionSocketHolder {
    private static final Map<UserClientDto, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MqMessageProducer mqMessageProducer;

    /**
     * 保存Channel，支持多端登陆，需要区分当前用户不同的设备登陆的channel
     *
     * @param appId
     * @param userId
     * @param clientType
     * @param imei
     * @param channel
     */
    public static void put(Integer appId, String userId, Integer clientType, String imei, NioSocketChannel channel) {
        UserClientDto dto = new UserClientDto();
        dto.setImei(imei);
        dto.setAppId(appId);
        dto.setClientType(clientType);
        dto.setUserId(userId);
        CHANNELS.put(dto, channel);
    }

    /**
     * 根据登录的信息，获取Channel
     *
     * @param appId
     * @param userId
     * @param clientType
     * @param imei
     * @return
     */
    public static NioSocketChannel get(Integer appId, String userId, Integer clientType, String imei) {
        UserClientDto dto = new UserClientDto();
        dto.setImei(imei);
        dto.setAppId(appId);
        dto.setClientType(clientType);
        dto.setUserId(userId);
        return CHANNELS.get(dto);
    }

    /**
     * 获取当前用户的（appid、userid）下所有登录的Channel
     *
     * @param appId
     * @param userId
     * @return
     */
    public static List<NioSocketChannel> get(Integer appId, String userId) {
        Set<UserClientDto> channelInfos = CHANNELS.keySet();
        List<NioSocketChannel> channels = new ArrayList<>();
        channelInfos.forEach(channel -> {
            if (channel.getAppId().equals(appId) && userId.equals(channel.getUserId())) {
                channels.add(CHANNELS.get(channel));
            }
        });
        return channels;
    }

    /**
     * 根据登录的详细信息删除 Channel
     *
     * @param appId
     * @param userId
     * @param clientType
     * @param imei
     */
    public static void remove(Integer appId, String userId, Integer clientType, String imei) {
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setImei(imei);
        dto.setClientType(clientType);
        dto.setUserId(userId);
        CHANNELS.remove(dto);
    }

    /**
     * 删除指定的 Channel，使用 Stream 过滤出需要删除的 Channel 的 key，再根据 key 去删除
     *
     * @param channel
     */
    public static void remove(NioSocketChannel channel) {
        CHANNELS.entrySet().stream().filter(entity -> entity.getValue() == channel)
                .forEach(entry -> CHANNELS.remove(entry.getKey()));
    }

    /**
     * 执行登出操作，需要删除本地的 Channel 缓存和Redis中的session缓存
     * 1、根据channel中的业务参数
     * 2、根据业务参数中的 appid userid clientType imei 删除指定的channel
     * 3、删除的Redis的中的sessions
     *
     * @param nioSocketChannel
     */
    public void removeUserSession(NioSocketChannel nioSocketChannel) {
        String userId = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.USERID)).get();
        Integer appId = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.APPID)).get();
        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
        String imei = (String) nioSocketChannel
                .attr(AttributeKey.valueOf(Constants.IMEI)).get();
        // 1、删除本地的 session
        SessionSocketHolder.remove(appId, userId, clientType, imei);

        // 2、构建登录端的标识
        String filed = clientType + ":" + imei;
        // 3、删除 redis 中对应端的的 session
        redisTemplate.opsForHash().delete(appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId, filed);

        // 4、通知 service 层的用户在线状态发生改变
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setAppId(appId);
        messageHeader.setImei(imei);
        messageHeader.setClientType(clientType);

        UserStatusChangeNotifyPack userStatusChangeNotifyPack = new UserStatusChangeNotifyPack();
        userStatusChangeNotifyPack.setAppId(appId);
        userStatusChangeNotifyPack.setUserId(userId);
        userStatusChangeNotifyPack.setStatus(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
        mqMessageProducer.sendMessage(userStatusChangeNotifyPack, UserEventCommand.USER_ONLINE_STATUS_CHANGE.getCommand(), messageHeader);
        // 5、关闭 channel
        nioSocketChannel.close();
    }

    public void offlineUserSession(NioSocketChannel nioSocketChannel) {
        String userId = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.USERID)).get();
        Integer appId = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.APPID)).get();
        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
        String imei = (String) nioSocketChannel
                .attr(AttributeKey.valueOf(Constants.IMEI)).get();
        // 1、超时关闭连接，删除本地缓存的 NioServerSocketChannel
        SessionSocketHolder.remove(appId, userId, clientType, imei);

        String strSession = (String) redisTemplate.opsForHash().get(appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId, clientType + ":" + imei);
        // 2、变更redis中的该客户端的在线状态
        if (StringUtils.isNotBlank(strSession)) {
            UserSession userSession = JSONObject.parseObject(strSession, UserSession.class);
            userSession.setConnectState(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            redisTemplate.opsForHash().put(appId +
                    Constants.RedisConstants.USER_SESSION_CONSTANTS + userId, clientType + ":" + imei, JSONObject.toJSONString(userSession));
        }

        // 3、通知 service 业务层业务端发生改变
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setAppId(appId);
        messageHeader.setImei(imei);
        messageHeader.setClientType(clientType);

        UserStatusChangeNotifyPack userStatusChangeNotifyPack = new UserStatusChangeNotifyPack();
        userStatusChangeNotifyPack.setAppId(appId);
        userStatusChangeNotifyPack.setUserId(userId);
        userStatusChangeNotifyPack.setStatus(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
        mqMessageProducer.sendMessage(userStatusChangeNotifyPack, UserEventCommand.USER_ONLINE_STATUS_CHANGE.getCommand(), messageHeader);
        // 5、channel
        nioSocketChannel.close();

    }
}
