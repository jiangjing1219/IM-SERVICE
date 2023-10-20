package com.jiangjing.im.service.utils;

import com.alibaba.fastjson2.JSON;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.ImConnectStatusEnum;
import com.jiangjing.im.common.model.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 获取用户的 session 的工具类，前提是，在用户登录的时候已经将用户 session 保存到redis中了
 * key:  appid :userSession:userid
 * field:  clientType:imei
 * value:  UserSession
 *
 * @author Admin
 */
@Component
public class UserSessionUtils {

    @Autowired
    RedisTemplate redisTemplate;


    /**
     * 获取当前用户下的所有在线的session
     *
     * @param appId
     * @param userId
     * @return
     */
    public List<UserSession> getUserSession(Integer appId, String userId) {
        // 构建 session 的 key
        String key = appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId;
        // 获取的 key 下的所有的信心，封装成 对象 返回 ：  field - value
        ArrayList<UserSession> userSessions = new ArrayList<>();
        Map<Object, Object> sessions = redisTemplate.opsForHash().entries(key);
        sessions.values().forEach(session -> {
            String userSessionStr = (String) session;
            UserSession userSession = JSON.parseObject(userSessionStr, UserSession.class);
            if (Objects.equals(userSession.getConnectState(), ImConnectStatusEnum.ONLINE_STATUS.getCode())) {
                userSessions.add(userSession);
            }
        });
        return userSessions;
    }

    /**
     * 获取该用户指定端的 session
     *
     * @param appId
     * @param userId
     * @param clientType
     * @param imei
     * @return
     */
    public UserSession getUserSession(Integer appId, String userId, Integer clientType, String imei) {
        String sessionKey = appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId;
        String field = clientType + ":" + imei;
        Object userSession = redisTemplate.opsForHash().get(sessionKey, field);
        if (userSession == null) {
            return null;
        }
        return JSON.parseObject(userSession.toString(), UserSession.class);
    }
}
