package com.jiangjing.im.service.message.service;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.DelFlagEnum;
import com.jiangjing.im.common.enums.command.GroupEventCommand;
import com.jiangjing.im.common.model.ClientInfo;
import com.jiangjing.im.common.model.message.GroupChatMessageContent;
import com.jiangjing.im.common.model.message.MessageContent;
import com.jiangjing.im.common.model.message.OfflineMessageContent;
import com.jiangjing.im.service.group.model.req.SendGroupMessageReq;
import com.jiangjing.im.service.group.service.ImGroupMemberService;
import com.jiangjing.im.service.message.model.resp.SendMessageResp;
import com.jiangjing.im.service.sequence.RedisSeq;
import com.jiangjing.im.service.utils.MessageProducer;
import com.jiangjing.im.service.utils.ThreadPoolExecutorUtils;
import com.jiangjing.pack.message.ChatMessageAck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 群聊消息的处理的 Service
 *
 * @author Admin
 */
@Service
public class GroupMessageService {

    private static final Logger logger = LoggerFactory.getLogger(GroupMessageService.class);

    @Autowired
    CheckSendMessageService checkSendMessageService;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    ImGroupMemberService imGroupMemberService;

    @Autowired
    MessageStoreService messageStoreService;

    @Autowired
    RedisSeq redisSeq;

    @Autowired
    MessageCacheService messageCacheService;

    public void process(GroupChatMessageContent messageContent) {

        // 查询消息缓存,如果存在消息缓存，只需要重新出发消息分配
        GroupChatMessageContent messageFromMessageIdCache = messageCacheService.getMessageFromMessageIdCache(messageContent.getAppId(), messageContent.getMessageId(), GroupChatMessageContent.class);
        if (messageFromMessageIdCache != null) {
            // 回复一个成功的ack
            ack(messageContent, ResponseVO.successResponse());
            // 同步发送者的其他端
            syncToSender(messageFromMessageIdCache, messageFromMessageIdCache);
            // 获取其他群成员，分发消息
            dispatchMessage(messageFromMessageIdCache);
            return;
        }


        // key 直接使用的 group 即可，保证消息的有序性
        long seq = redisSeq.getSeq(messageContent.getAppId() + Constants.SeqConstants.MESSAGE_SEQ + messageContent.getGroupId());
        messageContent.setMessageSequence(seq);
       /* String groupId = messageContent.getGroupId();
        String fromId = messageContent.getFromId();
        Integer appId = messageContent.getAppId();*/
        // 校验当前用户是否合法（NettyHandler 已经提前校验）
        /*ResponseVO responseVO = checkSendMessageService.checkGroupMessage(fromId, groupId, appId);
        if (responseVO.isOk()) {*/
        // 线程池优化
        ThreadPoolExecutorUtils.THREAD_POOL_EXECUTOR.execute(() -> {
            // 0、消息持久化
            messageStoreService.storeGroupMessage(messageContent);
            // 0-1、缓存群组的离线消息，写扩散
            List<String> memberIds = imGroupMemberService.getGroupMemberIds(messageContent.getGroupId(), messageContent.getAppId());
            messageContent.setMemberIds(memberIds);
            OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
            BeanUtils.copyProperties(messageContent,offlineMessageContent);
            offlineMessageContent.setDelFlag(DelFlagEnum.NORMAL.getCode());
            messageStoreService.storeGroupOfflineMessage(offlineMessageContent,memberIds);
            // 回复一个成功的ack
            ack(messageContent, ResponseVO.successResponse());
            // 同步发送者的其他端
            syncToSender(messageContent, messageContent);
            // 获取其他群成员，分发消息
            dispatchMessage(messageContent);
            // 缓存消息
            messageCacheService.setMessageFromMessageIdCache(messageContent.getAppId(), messageContent.getMessageId(), messageContent);
        });
      /*  } else {
            // 自己回复一个失败的 ack
            ack(messageContent, responseVO);
        }*/
    }


    private void ack(MessageContent messageContent, ResponseVO responseVO) {
        logger.info("msg ack,msgId={},checkResult{}", messageContent.getMessageId(), responseVO.getCode());
        ChatMessageAck chatMessageAck = new
                ChatMessageAck(messageContent.getMessageId(), messageContent.getMessageSequence());
        // 在给发送端回复ack时，携带消息本身的id信息，标识是那条消息的ack，前端方可操作
        responseVO.setData(chatMessageAck);
        // 回复一个 ack 给指定的端
        messageProducer.sendToUserByOne(messageContent.getFromId(), GroupEventCommand.GROUP_MSG_ACK, responseVO, messageContent);
    }

    /**
     * 同步消息给其他在线端
     *
     * @param messageContent
     * @param clientInfo
     */
    private void syncToSender(MessageContent messageContent, ClientInfo clientInfo) {
        messageProducer.sendToUserExceptClient(messageContent.getFromId(), GroupEventCommand.MSG_GROUP, messageContent, clientInfo);
    }

    /**
     * 发送消息给其他群成员
     *
     * @param messageContent
     */
    private void dispatchMessage(GroupChatMessageContent messageContent) {
        for (String memberId : messageContent.getMemberIds()) {
            if (!memberId.equals(messageContent.getFromId())) {
                messageProducer.sendToUserByAll(memberId, messageContent.getAppId(), GroupEventCommand.MSG_GROUP, messageContent);
            }
        }
    }

    /**
     * 群聊消息发送接口
     *
     * @param req
     * @return
     */
    public SendMessageResp sendMessage(SendGroupMessageReq req) {
        GroupChatMessageContent messageContent = new GroupChatMessageContent();
        BeanUtils.copyProperties(req, messageContent);
        // 1、消息持久化
        messageStoreService.storeGroupMessage(messageContent);
        // 2、同步发送者的其他端
        syncToSender(messageContent, messageContent);
        // 3、获取其他群成员，分发消息
        dispatchMessage(messageContent);
        SendMessageResp sendMessageResp = new SendMessageResp();
        sendMessageResp.setMessageKey(messageContent.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());
        return sendMessageResp;
    }
}
