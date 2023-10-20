package com.jiangjing.im.service.sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 获取 redis 计数器
 *  会话消息 KEY 设计：   APPID:messageSeq:fromId:toId   /   APPID:messageSeq:fromId:toId    (fromId  toId 大的放在前面)
 *  会话 KEY 设计（定制）： APPID：conversation
 *  好友关系 KEY 设计：    APPID：friendship
 * @author
 */
@Component
public class RedisSeq {

    @Autowired
    RedisTemplate redisTemplate;

    public long getSeq(String key){
        return redisTemplate.opsForValue().increment(key);
    }
}
