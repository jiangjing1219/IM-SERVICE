package com.jiangjing.im.service.message.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.ConversationTypeEnum;
import com.jiangjing.im.common.enums.DelFlagEnum;
import com.jiangjing.im.common.enums.MessageErrorCode;
import com.jiangjing.im.common.enums.command.GroupEventCommand;
import com.jiangjing.im.common.enums.command.MessageCommand;
import com.jiangjing.im.common.model.ClientInfo;
import com.jiangjing.im.common.model.SyncReq;
import com.jiangjing.im.common.model.SyncResp;
import com.jiangjing.im.common.model.message.*;
import com.jiangjing.im.service.conversation.service.ConversationService;
import com.jiangjing.im.service.group.service.ImGroupMemberService;
import com.jiangjing.im.service.message.dao.ImGroupMessageHistoryEntity;
import com.jiangjing.im.service.message.dao.ImMessageBodyEntity;
import com.jiangjing.im.service.message.dao.ImMessageHistoryEntity;
import com.jiangjing.im.service.message.dao.mapper.ImGroupMessageHistoryMapper;
import com.jiangjing.im.service.message.dao.mapper.ImMessageBodyMapper;
import com.jiangjing.im.service.message.dao.mapper.ImMessageHistoryMapper;
import com.jiangjing.im.service.message.model.req.GroupMessageHistoryReq;
import com.jiangjing.im.service.message.model.req.P2pMessageHistoryReq;
import com.jiangjing.im.service.message.model.resp.GroupMessageHistoryResp;
import com.jiangjing.im.service.message.model.resp.P2pMessageHistoryResp;
import com.jiangjing.im.service.sequence.RedisSeq;
import com.jiangjing.im.service.utils.ConversationIdGenerate;
import com.jiangjing.im.service.utils.GroupMessageProducer;
import com.jiangjing.im.service.utils.MessageProducer;
import com.jiangjing.im.service.utils.SnowflakeIdWorker;
import com.jiangjing.pack.message.MessageReadedPack;
import com.jiangjing.pack.message.RecallMessageNotifyPack;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 消息可靠性同步
 *
 * @author
 */
@Service
public class MessageSyncService {

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    ConversationService conversationService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ImMessageBodyMapper imMessageBodyMapper;

    @Autowired
    RedisSeq redisSeq;

    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    ImGroupMemberService imGroupMemberService;

    @Autowired
    GroupMessageProducer groupMessageProducer;

    @Autowired
    ImMessageHistoryMapper imMessageHistoryMapper;

    @Autowired
    ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;


    /**
     * 接收到消息接收端回复的 ack ，需要发送给指定的客户端
     *
     * @param receiveAckContent
     */
    public void receiveMark(MessageReceiveAckContent receiveAckContent) {
        // toId 是原消息的发送方
        messageProducer.sendToUserByAll(receiveAckContent.getToId(), receiveAckContent.getAppId(), MessageCommand.MSG_RECEIVE_ACK, receiveAckContent);
    }

    /**
     * 接收到接收端发来的已读标识
     * 1、同步给接收用户的其他端，将其他端的标识也置为已读
     * 2、更新 会话 表，已读消息的 messageSeq
     * 3、给发送端发送接收端已读的标识
     *
     * @param messageContent
     */
    public void readMark(MessageReadedContent messageContent) {
        // 1、更新 会话的已读信息
        conversationService.messageMarkRead(messageContent);
        // 2、接收端消息已读同步
        MessageReadedPack messageReadedPack = new MessageReadedPack();
        BeanUtils.copyProperties(messageContent, messageReadedPack);
        messageProducer.sendToUserExceptClient(messageReadedPack.getFromId(), MessageCommand.MSG_READED_SYNC, messageReadedPack, messageContent);
        // 3、发送给该消息的发送方
        messageProducer.sendToUserByAll(messageContent.getToId(), messageContent.getAppId(), MessageCommand.MSG_READED_RECEIPT, messageReadedPack);
    }

    /**
     * 接收到群组消息已读标识
     * 1、更数据库的会话信息，将接收方的会话已读的消息seq，更新的到当前消息
     * 2、同步接收端的已读标识
     * 3、通知发送端消息已读
     *
     * @param messageContent
     */
    public void groupReadMark(MessageReadedContent messageContent) {
        // 1、更新会话信息
        conversationService.messageMarkRead(messageContent);
        // 2、通知其他端该消息已读
        MessageReadedPack messageReadedPack = new MessageReadedPack();
        BeanUtils.copyProperties(messageContent, messageReadedPack);
        messageProducer.sendToUserExceptClient(messageContent.getFromId(), GroupEventCommand.MSG_GROUP_READED_SYNC, messageReadedPack, messageContent);
        // 3、通知发送端该消息已读,(排除自己发的消息，在其端读取)
        if (!messageContent.getFromId().equals(messageContent.getToId())) {
            messageProducer.sendToUserByAll(messageContent.getToId(), messageContent.getAppId(), GroupEventCommand.MSG_GROUP_READED_RECEIPT, messageReadedPack);
        }
    }

    /**
     * 同步离线消息：在持久化消息之后立刻将消息缓存在redis中了，使用 zset 结构存储，权重的是 messageKey，消息的key为  appId:offlineMessage:fromId  群聊消息和单聊消息保持一致，都是采用写扩散的方式，消息保存在自己的队列里面
     *
     * @param req
     * @return
     */
    public ResponseVO syncOfflineMessage(SyncReq req) {
        SyncResp<OfflineMessageContent> resp = new SyncResp<>();
        // 1、组装该用户的消息队列的 key
        String queueKey = req.getAppId() + Constants.RedisConstants.OFFLINE_MESSAGE + req.getOperate();
        Long maxSeq = 0L;
        // 2、获取离线消息的最大 sequence
        Set<ZSetOperations.TypedTuple<String>> set = redisTemplate.opsForZSet().reverseRangeWithScores(queueKey, 0, 0);
        // 3、获取具体的值
        if (!CollectionUtils.isEmpty(set)) {
            ZSetOperations.TypedTuple<String> tuple = set.iterator().next();
            maxSeq = Objects.requireNonNull(tuple.getScore()).longValue();
        }
        List<OfflineMessageContent> respList = new ArrayList<>();
        resp.setMaxSequence(maxSeq);
        Set<ZSetOperations.TypedTuple> querySet = redisTemplate.opsForZSet().rangeByScoreWithScores(queueKey, req.getLastSequence(), maxSeq, 0, req.getMaxLimit());
        for (ZSetOperations.TypedTuple<String> typedTuple : querySet) {
            String value = typedTuple.getValue();
            OfflineMessageContent offlineMessageContent = JSONObject.parseObject(value, OfflineMessageContent.class);
            respList.add(offlineMessageContent);
        }
        resp.setDataList(respList);
        if (!CollectionUtils.isEmpty(respList)) {
            OfflineMessageContent offlineMessageContent = respList.get(respList.size() - 1);
            resp.setCompleted(maxSeq <= offlineMessageContent.getMessageKey());
        } else {
            resp.setCompleted(true);
        }
        return ResponseVO.successResponse(resp);
    }

    /**
     * 撤回消息
     * 1、修改历史消息（写扩散/读扩散 都是将消息关系和 messageBody 分开存放的，如果需要撤回只需要将messageBody的状态修改为撤回状态即可）
     * 2、修改离线消息（单聊消息和群组消息都是将消息保存的自己的 zset 离线消息队列里面的）
     * 3、同步客户端
     * 4、通知接收端撤回消息
     *
     * @param messageContent
     */
    public void recallMessage(RecallMessageContent messageContent) {

        Long messageTime = messageContent.getMessageTime();
        Long now = System.currentTimeMillis();

        RecallMessageNotifyPack pack = new RecallMessageNotifyPack();
        BeanUtils.copyProperties(messageContent, pack);

        // 1、判断消息的发送时间，超过 2 分钟的消息不允许撤回
        if (120000L < now - messageTime) {
            // 回复 ack
            recallAck(pack, ResponseVO.errorResponse(MessageErrorCode.MESSAGE_RECALL_TIME_OUT), messageContent);
            return;
        }

        QueryWrapper<ImMessageBodyEntity> query = new QueryWrapper<>();
        query.eq("app_id", messageContent.getAppId());
        query.eq("message_key", messageContent.getMessageKey());
        ImMessageBodyEntity messageBody = imMessageBodyMapper.selectOne(query);
        if (messageBody == null) {
            // 不存在的消息不能撤回
            recallAck(pack, ResponseVO.errorResponse(MessageErrorCode.MESSAGEBODY_IS_NOT_EXIST), messageContent);
            return;
        }
        // 将 messageBody 置为无效
        messageBody.setDelFlag(DelFlagEnum.DELETE.getCode());
        imMessageBodyMapper.update(messageBody, query);

        // 2、单聊消息撤回(采用的是写扩散的方式，发送方接收方各自维护自己的消息列表，messageBody 只保存一份，删除只需将 messageBody 置为无效即可)
        if (messageContent.getConversationType() == ConversationTypeEnum.P2P.getCode()) {
            // 1、修改离线消息（给各自的消息列表插入一条消息撤回的新消息）
            String formQueue = messageContent.getAppId() + Constants.RedisConstants.OFFLINE_MESSAGE + messageContent.getFromId();
            String toQueue = messageContent.getAppId() + Constants.RedisConstants.OFFLINE_MESSAGE + messageContent.getToId();
            OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
            BeanUtils.copyProperties(messageContent, offlineMessageContent);
            offlineMessageContent.setDelFlag(DelFlagEnum.DELETE.getCode());
            offlineMessageContent.setConversationType(ConversationTypeEnum.P2P.getCode());
            offlineMessageContent.setConversationId(conversationService.convertConversationId(offlineMessageContent.getConversationType()
                    , messageContent.getFromId(), messageContent.getToId()));
            offlineMessageContent.setMessageBody(messageBody.getMessageBody());
            // 更新消息的序列
            long seq = redisSeq.getSeq(messageContent.getAppId() + Constants.SeqConstants.MESSAGE_SEQ + ConversationIdGenerate.generateP2PId(messageContent.getFromId(), messageContent.getToId()));
            offlineMessageContent.setMessageSequence(seq);
            // 生成新的  messageKey 作为 zset 的权重
            long messageKey = snowflakeIdWorker.nextId();
            // 新增历史消息
            redisTemplate.opsForZSet().add(formQueue, JSON.toJSONString(offlineMessageContent), messageKey);
            redisTemplate.opsForZSet().add(toQueue, JSON.toJSONString(offlineMessageContent), messageKey);

            // 回复撤回成功的 ack
            recallAck(pack, ResponseVO.successResponse(pack), messageContent);

            // 同步其他端
            messageProducer.sendToUserExceptClient(messageContent.getFromId(), MessageCommand.MSG_RECALL_SYNC, pack, messageContent);

            // 通知接收端
            messageProducer.sendToUserByAll(messageContent.getToId(), messageContent.getAppId(), MessageCommand.MSG_RECALL_NOTIFY, pack);
            // 群聊消息的撤回
        } else {
            // 1、获取所有的群成员id，每个群成员的离线消息队列都需要发起撤回消息命令
            List<String> memberIds = imGroupMemberService.getGroupMemberIds(messageContent.getToId(), messageContent.getAppId());
            // 2、回复撤销操作成功的ack
            recallAck(pack, ResponseVO.successResponse(pack), messageContent);
            // 3、同步其他的登录端
            messageProducer.sendToUserExceptClient(messageContent.getFromId(), MessageCommand.MSG_RECALL_SYNC, pack, messageContent);
            for (String memberId : memberIds) {
                String messageQueueKey = messageContent.getAppId() + Constants.RedisConstants.OFFLINE_MESSAGE + memberId;
                OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
                BeanUtils.copyProperties(messageContent, offlineMessageContent);
                offlineMessageContent.setDelFlag(DelFlagEnum.DELETE.getCode());
                offlineMessageContent.setConversationType(ConversationTypeEnum.P2P.getCode());
                offlineMessageContent.setConversationId(conversationService.convertConversationId(offlineMessageContent.getConversationType()
                        , messageContent.getFromId(), messageContent.getToId()));
                offlineMessageContent.setMessageBody(messageBody.getMessageBody());
                // 更新消息的序列
                long seq = redisSeq.getSeq(messageContent.getAppId() + Constants.SeqConstants.MESSAGE_SEQ + messageContent.getToId());
                offlineMessageContent.setMessageSequence(seq);
                // 生成新的  messageKey 作为 zset 的权重
                long messageKey = snowflakeIdWorker.nextId();
                // 新增历史消息
                redisTemplate.opsForZSet().add(messageQueueKey, JSON.toJSONString(offlineMessageContent), messageKey);
            }
            // 通知接收端  todo 弊端，自身接收到的命令应该是 MSG_RECALL_SYNC ，优化逻辑
            groupMessageProducer.sendMessage(messageContent.getFromId(), messageContent.getAppId(), messageContent.getToId(), MessageCommand.MSG_RECALL_NOTIFY, pack, messageContent);
        }
    }

    /**
     * 撤回消息，返回撤回成功的 ack
     *
     * @param pack
     * @param result
     * @param clientInfo
     */
    private void recallAck(RecallMessageNotifyPack pack, ResponseVO<Object> result, ClientInfo clientInfo) {
        messageProducer.sendToUserByOne(pack.getFromId(), MessageCommand.MSG_RECALL_ACK, result, clientInfo);
    }

    /**
     * 查询单聊消息的聊天历史记录
     *
     * @param req
     * @return
     */
    public List<P2pMessageHistoryResp> queryP2pMessageHistory(P2pMessageHistoryReq req) {
        ArrayList<P2pMessageHistoryResp> p2pMessageHistoryResps = new ArrayList<>();
        List<ImMessageHistoryEntity> messageHistoryEntityList = imMessageHistoryMapper.queryMessageHistory(req);
        for (ImMessageHistoryEntity imMessageHistoryEntity : messageHistoryEntityList) {
            P2pMessageHistoryResp resp = new P2pMessageHistoryResp();
            BeanUtils.copyProperties(imMessageHistoryEntity, resp);
            // 查询 messageBody 信息
            QueryWrapper<ImMessageBodyEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("message_key", imMessageHistoryEntity.getMessageKey());
            queryWrapper.eq("app_id", imMessageHistoryEntity.getAppId());
            ImMessageBodyEntity messageBody = imMessageBodyMapper.selectOne(queryWrapper);
            if (messageBody != null) {
                resp.setMessageBody(messageBody.getMessageBody());
                resp.setDelFlag(messageBody.getDelFlag());
                p2pMessageHistoryResps.add(resp);
            }
        }
        return p2pMessageHistoryResps;
    }

    /**
     * 查询群聊历史
     *
     * @param req
     * @return
     */
    public List<GroupMessageHistoryResp> queryGroupMessageHistory(GroupMessageHistoryReq req) {
        ArrayList<GroupMessageHistoryResp> p2pMessageHistoryResps = new ArrayList<>();
        List<ImGroupMessageHistoryEntity> messageHistoryEntityList = imGroupMessageHistoryMapper.queryMessageHistory(req);
        for (ImGroupMessageHistoryEntity imMessageHistoryEntity : messageHistoryEntityList) {
            GroupMessageHistoryResp resp = new GroupMessageHistoryResp();
            BeanUtils.copyProperties(imMessageHistoryEntity, resp);
            // 查询 messageBody 信息
            QueryWrapper<ImMessageBodyEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("message_key", imMessageHistoryEntity.getMessageKey());
            queryWrapper.eq("app_id", imMessageHistoryEntity.getAppId());
            ImMessageBodyEntity messageBody = imMessageBodyMapper.selectOne(queryWrapper);
            if (messageBody != null) {
                p2pMessageHistoryResps.add(resp);
            }
        }
        return p2pMessageHistoryResps;
    }
}
