package com.jiangjing.im.service.sequence;

import com.jiangjing.im.common.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 在用户离线登录时，需要拉取：1、好友关系列表 2、会话信息  2、群组信息  需要优化拉取的方式
 * 优化：
 * 拉取方式：1、间歇拉取，用户遇到哪些就拉取哪些（分摊拉取时间） 2、按需拉取（只拉取增量修改部分）
 * 按需拉取的实现：
 * （1）、该好友信息、群组信息、会话信息 添加 seq 序列号/本版号
 * （2）、缓存单个用户的序列胡奥信息（使用 redis的 hash 结构）
 * （3）、客户记录当前业务最大的seq，查询增量信息时需要过滤版本号
 *
 * @author
 */
@Component
public class WriteUserSeq {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * key = appid : seq : userid   seqType   seqValue
     *
     *  seqValue =
     * @param appId
     * @param userId
     * @param seqType
     * @param seq
     */
    public void writeUserSeq(Integer appId, String userId, String seqType, Long seq) {
        String key = appId + Constants.RedisConstants.SEQ_PREFIX + userId;
        redisTemplate.opsForHash().put(key, seqType, seq);
    }

}
