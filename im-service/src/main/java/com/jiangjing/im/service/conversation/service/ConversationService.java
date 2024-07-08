package com.jiangjing.im.service.conversation.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.ConversationErrorCode;
import com.jiangjing.im.common.enums.ConversationTypeEnum;
import com.jiangjing.im.common.enums.command.ConversationEventCommand;
import com.jiangjing.im.common.model.ClientInfo;
import com.jiangjing.im.common.model.SyncReq;
import com.jiangjing.im.common.model.SyncResp;
import com.jiangjing.im.common.model.message.MessageReadedContent;
import com.jiangjing.im.service.conversation.dao.ImConversationSetEntity;
import com.jiangjing.im.service.conversation.dao.mapper.ImConversationSetMapper;
import com.jiangjing.im.service.conversation.model.DeleteConversationReq;
import com.jiangjing.im.service.conversation.model.UpdateConversationReq;
import com.jiangjing.im.service.sequence.RedisSeq;
import com.jiangjing.im.service.sequence.WriteUserSeq;
import com.jiangjing.im.service.utils.MessageProducer;
import com.jiangjing.pack.conversation.DeleteConversationPack;
import com.jiangjing.pack.conversation.UpdateConversationPack;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ConversationService {

    @Autowired
    ImConversationSetMapper imConversationSetMapper;

    @Autowired
    RedisSeq redisSeq;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    WriteUserSeq writeUserSeq;

    /**
     * 生成一个会话id：conversationType_fromId_toId 的形式
     *
     * @param type
     * @param fromId
     * @param toId
     * @return
     */
    public String convertConversationId(Integer type, String fromId, String toId) {
        return type + "_" + fromId + "_" + toId;
    }

    /**
     * 消息已读 - 更新的
     * formId 是 该消息的接收方
     * toId 是 该消息的发送方
     * @param messageContent
     */
    public void messageMarkRead(MessageReadedContent messageContent) {
        String toId = messageContent.getFromId();
        // 群组的会话，toId 是 groupID
        if (messageContent.getConversationType() == ConversationTypeEnum.GROUP.getCode()) {
            toId = messageContent.getGroupId();
        }
        String fromId = messageContent.getToId();
        Integer conversationType = messageContent.getConversationType();
        // 更新接收方的会话信息
        String conversationId = convertConversationId(conversationType, toId, fromId);
        QueryWrapper<ImConversationSetEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("conversation_id", conversationId);
        queryWrapper.eq("app_id", messageContent.getAppId());
        ImConversationSetEntity imConversationSetEntity = imConversationSetMapper.selectOne(queryWrapper);
        // 获取会话的 SEQ   key= appid ：
        long seq = redisSeq.getSeq(messageContent.getAppId() + ":" + Constants.SeqConstants.CONVERSATION_SEQ);
        // 会话记录不存在则直接创建
        if (imConversationSetEntity == null) {
            ImConversationSetEntity conversationSet = new ImConversationSetEntity();
            BeanUtils.copyProperties(messageContent, conversationSet);
            conversationSet.setConversationId(conversationId);
            // 会话本身的 seq，增量来取的标识
            conversationSet.setSequence(seq);
            conversationSet.setFromId(fromId);
            conversationSet.setToId(toId);
            // 已读消息的 seq
            conversationSet.setReadedSequence(messageContent.getMessageSequence());
            imConversationSetMapper.insert(conversationSet);
        } else {
            // 存在会话记录直接更新（每次聊完天之后会话都会置顶）
            imConversationSetEntity.setSequence(seq);
            imConversationSetEntity.setReadedSequence(messageContent.getMessageSequence());
            imConversationSetMapper.readMark(imConversationSetEntity);
        }
        // 更新缓存（涉及增量更新的时候使用的）
        writeUserSeq.writeUserSeq(messageContent.getAppId(), messageContent.getFromId(), Constants.SeqConstants.CONVERSATION_SEQ, seq);
    }

    /**
     * 删除会话
     * <p>
     * todo 删除会话本身是没有操作数库的，直接同步到了其他在线端，那么离线端该如何同步？
     *
     * @param req
     * @return
     */
    public ResponseVO deleteConversation(DeleteConversationReq req) {
        // 同步到其他端
        DeleteConversationPack pack = new DeleteConversationPack();
        pack.setConversationId(req.getConversationId());
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setClientType(req.getClientType());
        clientInfo.setAppId(req.getAppId());
        clientInfo.setImei(req.getImei());
        messageProducer.sendToUserExceptClient(req.getFromId(), ConversationEventCommand.CONVERSATION_DELETE, pack, clientInfo);
        return ResponseVO.successResponse();
    }

    /**
     * 更新会话/免打扰
     *
     * @param req
     * @return
     */
    public ResponseVO updateConversation(UpdateConversationReq req) {
        if (req.getIsMute() == null && req.getIsTop() == null) {
            // 直接返回错误
            ResponseVO.errorResponse(ConversationErrorCode.CONVERSATION_UPDATE_PARAM_ERROR);
        }
        // 查询会话信息
        QueryWrapper<ImConversationSetEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("conversation_id", req.getConversationId());
        queryWrapper.eq("app_id", req.getAppId());
        ImConversationSetEntity imConversationSetEntity = imConversationSetMapper.selectOne(queryWrapper);
        if (imConversationSetEntity != null) {
            // 操作之后，更新seq，增量数据标识
            long seq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.CONVERSATION_SEQ);
            imConversationSetEntity.setSequence(seq);
            // 更新置顶字段
            if (req.getIsTop() != null) {
                imConversationSetEntity.setIsTop(req.getIsTop());
            }
            // 更新免打扰字段
            if (req.getIsMute() != null) {
                imConversationSetEntity.setIsMute(req.getIsMute());
            }
            imConversationSetMapper.update(imConversationSetEntity, queryWrapper);
            // 同步缓存
            writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.CONVERSATION_SEQ, seq);
            // 同步到其他端
            UpdateConversationPack pack = new UpdateConversationPack();
            pack.setConversationId(req.getConversationId());
            pack.setConversationType(imConversationSetEntity.getConversationType());
            pack.setConversationId(req.getConversationId());
            pack.setIsTop(imConversationSetEntity.getIsTop());
            pack.setIsMute(imConversationSetEntity.getIsMute());
            pack.setSequence(seq);
            ClientInfo clientInfo = new ClientInfo();
            BeanUtils.copyProperties(req, clientInfo);
            messageProducer.sendToUserExceptClient(req.getFromId(), ConversationEventCommand.CONVERSATION_UPDATE, pack, clientInfo);
        }
        return ResponseVO.successResponse();
    }

    /**
     * 会话增量同步
     *
     * @param req
     * @return
     */
    public ResponseVO syncConversationSet(SyncReq req) {
        // 1、限制拉去的数量
        if (req.getMaxLimit() > 100) {
            req.setMaxLimit(100);
        }
        // 2、查询增量数据
        QueryWrapper<ImConversationSetEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId());
        queryWrapper.eq("from_id", req.getOperate());
        queryWrapper.gt("sequence", req.getLastSequence());
        queryWrapper.last("limit " + req.getMaxLimit());
        queryWrapper.orderByAsc("sequence");
        List<ImConversationSetEntity> conversationSetEntityList = imConversationSetMapper.selectList(queryWrapper);
        // 3、组装返回值
        SyncResp<ImConversationSetEntity> resp = new SyncResp<>();
        if (!conversationSetEntityList.isEmpty()) {
            ImConversationSetEntity maxConversationSetEntity = conversationSetEntityList.get(conversationSetEntityList.size() - 1);
            resp.setDataList(conversationSetEntityList);
            // 4、查询当前用户会话最大的 sequence
            Long conversationSetMaxSeq = imConversationSetMapper.getConversationSetMaxSeq(req.getAppId(), req.getOperate());
            resp.setMaxSequence(conversationSetMaxSeq);
            resp.setCompleted(Objects.equals(maxConversationSetEntity.getSequence(), conversationSetMaxSeq));
            return ResponseVO.successResponse(resp);
        }
        resp.setCompleted(true);
        resp.setDataList(Collections.EMPTY_LIST);
        resp.setMaxSequence(0L);
        return ResponseVO.successResponse(resp);
    }
}
