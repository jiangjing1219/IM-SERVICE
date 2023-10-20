package com.jiangjing.im.service.message.service;


import com.alibaba.fastjson2.JSON;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.ConversationTypeEnum;
import com.jiangjing.im.common.enums.DelFlagEnum;
import com.jiangjing.im.common.model.message.*;
import com.jiangjing.im.service.conversation.service.ConversationService;
import com.jiangjing.im.service.group.service.ImGroupMemberService;
import com.jiangjing.im.service.message.dao.ImGroupMessageHistoryEntity;
import com.jiangjing.im.service.message.dao.ImMessageBodyEntity;
import com.jiangjing.im.service.message.dao.ImMessageHistoryEntity;
import com.jiangjing.im.service.message.dao.mapper.ImGroupMessageHistoryMapper;
import com.jiangjing.im.service.message.dao.mapper.ImMessageBodyMapper;
import com.jiangjing.im.service.message.dao.mapper.ImMessageHistoryMapper;
import com.jiangjing.im.service.utils.SnowflakeIdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息持久化
 */
@Transactional
@Service
public class MessageStoreService {

    @Autowired
    ImMessageBodyMapper imMessageBodyMapper;

    @Autowired
    ImMessageHistoryMapper imMessageHistoryMapper;

    @Autowired
    ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;

    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ConversationService conversationService;

    @Autowired
    ImGroupMemberService imGroupMemberService;

    /**
     * 单聊消息持久化：采用的是 修扩散的方式，即一条消息需要给双方的用户插入
     * <p>
     * 优化策略：直接发送给 MQ
     *
     * @param messageContent
     */
    public void storeP2pMessage(MessageContent messageContent) {
        // 1、插入 messageBody 信息（优化：Body 只存一份，消息关系保存多份，ImMessageBodyEntity 是数据库实体类，发送给 MQ 时需要使用一般类）
      /*ImMessageBodyEntity imMessageBodyEntity = extractMessageBody(messageContent);
        imMessageBodyMapper.insert(imMessageBodyEntity);
        // 2、获取写扩散需要的插入的记录信息
        List<ImMessageHistoryEntity> imMessageHistoryEntities = extractToP2pMessageHistory(messageContent, imMessageBodyEntity);
        imMessageHistoryMapper.insertBatchSomeColumn(imMessageHistoryEntities);
        messageContent.setMessageKey(imMessageBodyEntity.getMessageKey());*/
        ImMessageBody imMessageBody = extractMessageBody(messageContent);
        DoStoreP2PMessageDto doStoreP2PMessageDto = new DoStoreP2PMessageDto();
        doStoreP2PMessageDto.setMessageBody(imMessageBody);
        doStoreP2PMessageDto.setMessageContent(messageContent);
        // 直接投递到 MQ，Exchange = storeP2PMessage,routingKey = storeP2PMessage
        messageContent.setMessageKey(imMessageBody.getMessageKey());
        rabbitTemplate.convertAndSend(Constants.RabbitConstants.STORE_P2P_MESSAGE, Constants.RabbitConstants.STORE_P2P_MESSAGE, JSON.toJSONString(doStoreP2PMessageDto));
    }

    private List<ImMessageHistoryEntity> extractToP2pMessageHistory(MessageContent messageContent, ImMessageBodyEntity imMessageBodyEntity) {
        ArrayList<ImMessageHistoryEntity> list = new ArrayList<>();
        ImMessageHistoryEntity historyEntityFrom = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, historyEntityFrom);
        historyEntityFrom.setOwnerId(messageContent.getFromId());
        historyEntityFrom.setMessageKey(imMessageBodyEntity.getMessageKey());
        historyEntityFrom.setCreateTime(System.currentTimeMillis());
        list.add(historyEntityFrom);

        ImMessageHistoryEntity historyEntityTo = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, historyEntityTo);
        historyEntityTo.setOwnerId(messageContent.getToId());
        historyEntityTo.setMessageKey(imMessageBodyEntity.getMessageKey());
        historyEntityTo.setCreateTime(System.currentTimeMillis());
        list.add(historyEntityTo);
        return list;
    }

    private ImMessageBody extractMessageBody(MessageContent messageContent) {
        ImMessageBody imMessageBody = new ImMessageBody();
        imMessageBody.setAppId(messageContent.getAppId());
        // 现在才生成的 messageId，那前端 ack 是如何识别是哪一条消息的ack ??????
        imMessageBody.setMessageKey(snowflakeIdWorker.nextId());
        // 入库的时间
        imMessageBody.setCreateTime(System.currentTimeMillis());
        imMessageBody.setDelFlag(DelFlagEnum.NORMAL.getCode());
        imMessageBody.setMessageBody(messageContent.getMessageBody());
        imMessageBody.setExtra(messageContent.getExtra());
        imMessageBody.setSecurityKey("");
        // 发送消息的时间
        imMessageBody.setMessageTime(messageContent.getMessageTime());
        return imMessageBody;
    }

    /**
     * 群聊消息的持久化，采用读扩散的方式
     *
     * @param messageContent
     */
    public void storeGroupMessage(GroupChatMessageContent messageContent) {
        /*ImMessageBodyEntity imMessageBodyEntity = extractMessageBody(messageContent);
        imMessageBodyMapper.insert(imMessageBodyEntity);
        ImGroupMessageHistoryEntity imGroupMessageHistoryEntity = extractToGroupMessageHistory(messageContent, imMessageBodyEntity);
        imGroupMessageHistoryMapper.insert(imGroupMessageHistoryEntity);
        messageContent.setMessageKey(imMessageBodyEntity.getMessageKey());*/
        ImMessageBody imMessageBody = extractMessageBody(messageContent);
        DoStoreGroupMessageDto doStoreGroupMessageDto = new DoStoreGroupMessageDto();
        doStoreGroupMessageDto.setMessageBody(imMessageBody);
        doStoreGroupMessageDto.setGroupChatMessageContent(messageContent);
        messageContent.setMessageKey(imMessageBody.getMessageKey());
        // 直接投递到 MQ，Exchange = storeP2PMessage,routingKey = storeP2PMessage
        rabbitTemplate.convertAndSend(Constants.RabbitConstants.STORE_GROUP_MESSAGE, Constants.RabbitConstants.STORE_GROUP_MESSAGE, JSON.toJSONString(doStoreGroupMessageDto));

    }

    private ImGroupMessageHistoryEntity extractToGroupMessageHistory(GroupChatMessageContent messageContent, ImMessageBodyEntity messageBodyEntity) {
        ImGroupMessageHistoryEntity result = new ImGroupMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, result);
        result.setGroupId(messageContent.getGroupId());
        result.setMessageKey(messageBodyEntity.getMessageKey());
        result.setCreateTime(System.currentTimeMillis());
        return result;
    }

    /**
     * 缓存离线消息（无论用户是偶由在线端都是需要缓存离线消息的，如果有其他端重新登录，会拉取离线消息的列表，根据自身接收到的最大的 messageKey，读取大于该 messageKey 的消息）
     * 1、采用写扩散的方式，没有用户维护自己的消息队列（Redis 的 zset 结构，以 messageKey 作为权重进行排序）
     * 2、消息队列（Redis 的 zset 结构，以 messageKey 作为权重进行排序）
     * 3、接收到消息时，需要的判断消息总量是否超出限制。（时间维度，只保留七天的消息。数量为度，只保留 1000 条）
     * 4、如果超过限制，删除最老的消息再插入新的消息
     *
     * @param offlineMessageContent
     */
    public void storeOfflineMessage(OfflineMessageContent offlineMessageContent) {
        // 1、找到当前发送用户的消息队列   appId : offlineMessage : fromId
        String formQueueKey = offlineMessageContent.getAppId() + Constants.RedisConstants.OFFLINE_MESSAGE + offlineMessageContent.getFromId();
        // 2、判断时候超出限制
        Long totalCount = redisTemplate.opsForZSet().zCard(formQueueKey);
        if (totalCount > 1000) {
            // 删除第一个元素
            redisTemplate.opsForZSet().removeRange(formQueueKey, 0, 0);
        }
        // 3、拼装  ConversationId =  FROMID_TOID
        offlineMessageContent.setConversationId(conversationService.convertConversationId(
                ConversationTypeEnum.P2P.getCode(), offlineMessageContent.getFromId(), offlineMessageContent.getToId()
        ));
        // 4、保存发送端消息
        redisTemplate.opsForZSet().add(formQueueKey, JSON.toJSONString(offlineMessageContent), offlineMessageContent.getMessageKey());

        // 5、获取接收端用户的队列
        String toQueueKey = offlineMessageContent.getAppId() + Constants.RedisConstants.OFFLINE_MESSAGE + offlineMessageContent.getToId();
        // 6、判断限制
        Long toTotalCount = redisTemplate.opsForZSet().zCard(toQueueKey);
        if (toTotalCount > 1000) {
            // 删除第一个元素
            redisTemplate.opsForZSet().removeRange(toQueueKey, 0, 0);
        }
        // 7、获取接收的端的会话ID  TOId_FORMID
        offlineMessageContent.setConversationId(conversationService.convertConversationId(
                ConversationTypeEnum.P2P.getCode(), offlineMessageContent.getToId(), offlineMessageContent.getFromId()));
        // 8、保存接收端消息
        redisTemplate.opsForZSet().add(toQueueKey, JSON.toJSONString(offlineMessageContent), offlineMessageContent.getMessageKey());
    }

    /**
     * 群组消息的离线消息处理（写扩散的方式）
     * 1、获取当前用户的消息队列
     * 2、判断消息队列是否超出限制
     * 3、给发送者的队列添加新消息
     *
     * @param offlineMessageContent
     * @param memberIds             以及包含自己的 id 了
     */
    public void storeGroupOfflineMessage(OfflineMessageContent offlineMessageContent, List<String> memberIds) {
        offlineMessageContent.setConversationType(ConversationTypeEnum.GROUP.getCode());
        for (String memberId : memberIds) {
            String queueKey = offlineMessageContent.getAppId() + Constants.RedisConstants.OFFLINE_MESSAGE + memberId;
            Long totalCount = redisTemplate.opsForZSet().zCard(queueKey);
            if (totalCount > 1000) {
                // 删除第一个元素
                redisTemplate.opsForZSet().removeRange(queueKey, 0, 0);
            }
            // 3、拼装  ConversationId =  FROMID_TOID
            offlineMessageContent.setConversationId(conversationService.convertConversationId(
                    ConversationTypeEnum.GROUP.getCode(), memberId, offlineMessageContent.getGroupId()
            ));
            // 4、缓存消息
            redisTemplate.opsForZSet().add(queueKey, JSON.toJSONString(offlineMessageContent), offlineMessageContent.getMessageKey());
        }
    }
}
