package com.jiangjing.im.service.user.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.UserErrorCode;
import com.jiangjing.im.common.enums.command.UserEventCommand;
import com.jiangjing.im.common.exception.ApplicationException;
import com.jiangjing.im.common.model.ClientInfo;
import com.jiangjing.im.common.model.UserSession;
import com.jiangjing.im.service.friendship.service.ImFriendService;
import com.jiangjing.im.service.user.model.UserStatusChangeNotifyContent;
import com.jiangjing.im.service.user.model.req.PullFriendOnlineStatusReq;
import com.jiangjing.im.service.user.model.req.PullUserOnlineStatusReq;
import com.jiangjing.im.service.user.model.req.SetUserCustomerStatusReq;
import com.jiangjing.im.service.user.model.req.SubscribeUserOnlineStatusReq;
import com.jiangjing.im.service.user.model.resp.UserOnlineStatusResp;
import com.jiangjing.im.service.user.service.ImUserStatusService;
import com.jiangjing.im.service.utils.MessageProducer;
import com.jiangjing.im.service.utils.UserSessionUtils;
import com.jiangjing.pack.user.UserCustomStatusChangeNotifyPack;
import com.jiangjing.pack.user.UserStatusChangeNotifyPack;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ImUserStatusServiceImpl implements ImUserStatusService {

    @Autowired
    UserSessionUtils userSessionUtils;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    ImFriendService imFriendService;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 用户在线状态变更通知
     *
     * @param content
     */
    @Override
    public void processUserOnlineStatusNotify(UserStatusChangeNotifyContent content) {
        // 1、当前用户其他在线客户端
        List<UserSession> sessions = userSessionUtils.getUserSession(content.getAppId(), content.getUserId());
        // 2、通知其他客户端 - 在线状态对端同步
        UserStatusChangeNotifyPack notifyPack = new UserStatusChangeNotifyPack();
        BeanUtils.copyProperties(content, notifyPack);
        notifyPack.setClient(sessions);
        messageProducer.sendToUserExceptClient(content.getUserId(), UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY_SYNC, notifyPack, content);
        // 3、通知所有的好友和临时订阅该用户的在线状态的客户端
        dispatcher(notifyPack, content.getAppId(), content.getUserId(), UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY);
    }


    /**
     * 通知所有的好友和临时订阅该用户的在线状态的客户端
     *
     * @param notifyPack
     * @param appId
     * @param userId
     * @param userEventCommand
     */
    private void dispatcher(Object notifyPack, Integer appId, String userId, UserEventCommand userEventCommand) {
        // 1、获取所有的好友列表
        List<String> friendIds = imFriendService.getAllFriendId(appId, userId);
        // 2、推送给好友客户端
        for (String friendId : friendIds) {
            messageProducer.sendToUserByAll(friendId, appId, userEventCommand, notifyPack);
        }
        // 3、获取临时订阅当前用户在线状态的
        String userKey = appId + ":" + Constants.RedisConstants.SUBSCRIBE + userId;
        Set keys = redisTemplate.opsForHash().keys(userKey);
        for (Object key : keys) {
            String field = String.valueOf(key);
            long expire = Long.parseLong((String) Objects.requireNonNull(redisTemplate.opsForHash().get(userKey, field)));
            if (expire > 0 && expire > System.currentTimeMillis()) {
                messageProducer.sendToUserByAll(field, appId, userEventCommand, expire);
            } else {
                redisTemplate.opsForHash().delete(userKey, field);
            }
        }
    }

    /**
     * 临时订阅用户的在线状态，用户自身维护自身的额订阅列表
     *
     * @param req
     */
    @Override
    public void subscribeUserOnlineStatus(SubscribeUserOnlineStatusReq req) {
        // 计算有效时间
        Long subExpireTime = 0L;
        if (req != null && req.getSubTime() > 0) {
            subExpireTime = System.currentTimeMillis() + req.getSubTime();
        } else {
            throw new ApplicationException(UserErrorCode.SUB_TIME_ERROR);
        }
        for (String subUserId : req.getSubUserIds()) {
            String subKey = req.getAppId() + Constants.RedisConstants.SUBSCRIBE + subUserId;
            redisTemplate.opsForHash().put(subKey, req.getOperate(), subExpireTime);
        }
    }

    /***
     * 设置自定义在线状态
     *
     * @param req
     */
    @Override
    public void setUserCustomerStatus(SetUserCustomerStatusReq req) {
        // 1、更新缓存
        String cacheKey = req.getAppId() + Constants.RedisConstants.USER_CUSTOMER_STATUS + req.getUserId();
        UserCustomStatusChangeNotifyPack notifyPack = new UserCustomStatusChangeNotifyPack();
        BeanUtils.copyProperties(req, notifyPack);
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(notifyPack));
        // 2、同步在线端
        ClientInfo clientInfo = new ClientInfo();
        BeanUtils.copyProperties(req, clientInfo);
        messageProducer.sendToUserExceptClient(req.getUserId(), UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY_SYNC, notifyPack, clientInfo);
        // 3、通知临时订阅端
        dispatcher(notifyPack, req.getAppId(), req.getUserId(), UserEventCommand.USER_CUSTOM_STATUS_CHANGE_NOTIFY);
    }

    /**
     * 获取好友在线状态信息
     *
     * @param req
     * @return
     */
    @Override
    public Map<String, UserOnlineStatusResp> queryFriendOnlineStatus(PullFriendOnlineStatusReq req) {
        List<String> userIds = imFriendService.getAllFriendId(req.getAppId(), req.getUserId());
        return getUserOnlineStatus(userIds, req.getAppId());
    }

    /**
     * 获取执行用户的在线状态信息
     *
     * @param req
     * @return
     */
    @Override
    public Map<String, UserOnlineStatusResp> queryUserOnlineStatus(PullUserOnlineStatusReq req) {
        return getUserOnlineStatus(req.getUserIdList(), req.getAppId());
    }

    private Map<String, UserOnlineStatusResp> getUserOnlineStatus(List<String> idList, Integer appId) {
        HashMap<String, UserOnlineStatusResp> respHashMap = new HashMap<>(idList.size());
        for (String userId : idList) {
            UserOnlineStatusResp userOnlineStatusResp = new UserOnlineStatusResp();
            // 1、获取 session 在线状态信息
            List<UserSession> sessions = userSessionUtils.getUserSession(appId, userId);
            userOnlineStatusResp.setSession(sessions);
            userOnlineStatusResp.setOnlineStatus(CollectionUtils.isEmpty(sessions) ? 0 : 1);
            // 2、获取自定义状态信息
            String cacheKey = appId + Constants.RedisConstants.USER_CUSTOMER_STATUS + userId;
            String customStatusStr = (String) redisTemplate.opsForValue().get(cacheKey);
            UserCustomStatusChangeNotifyPack notifyPack = JSON.parseObject(customStatusStr, UserCustomStatusChangeNotifyPack.class);
            if (notifyPack != null) {
                BeanUtils.copyProperties(notifyPack, userOnlineStatusResp);
            }
            // 3、保存结果
            respHashMap.put(userId, userOnlineStatusResp);
        }
        return respHashMap;
    }
}
