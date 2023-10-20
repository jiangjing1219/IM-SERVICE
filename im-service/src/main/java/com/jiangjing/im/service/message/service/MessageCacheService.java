package com.jiangjing.im.service.message.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jiangjing.im.common.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 消息缓存相关操作
 *
 * @author
 */
@Service
public class MessageCacheService {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 接收到消息时创建 message 缓存
     * appid:cacheMessage:messageId
     *
     * @param appId
     * @param messageId
     * @param messageContent
     */
    public void setMessageFromMessageIdCache(Integer appId, String messageId, Object messageContent) {
        String cacheKey = appId + Constants.RedisConstants.CACHE_MESSAGE + messageId;
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(messageContent), 300, TimeUnit.SECONDS);
    }

    /**
     * 获取消息缓存
     *
     * @param appId
     * @param messageId
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getMessageFromMessageIdCache(Integer appId, String messageId, Class<T> clazz) {
        String cacheKey = appId + Constants.RedisConstants.CACHE_MESSAGE + messageId;
        String msg = (String) redisTemplate.opsForValue().get(cacheKey);
        if (msg == null) {
            return null;
        }
        return JSONObject.parseObject(msg, clazz);
    }
}
