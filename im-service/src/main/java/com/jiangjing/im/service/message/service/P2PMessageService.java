package com.jiangjing.im.service.message.service;

import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.config.AppConfig;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.ConversationTypeEnum;
import com.jiangjing.im.common.enums.DelFlagEnum;
import com.jiangjing.im.common.enums.command.MessageCommand;
import com.jiangjing.im.common.model.ClientInfo;
import com.jiangjing.im.common.model.message.MessageBody;
import com.jiangjing.im.common.model.message.MessageContent;
import com.jiangjing.im.common.model.message.OfflineMessageContent;
import com.jiangjing.im.service.message.model.req.SendMessageReq;
import com.jiangjing.im.service.message.model.resp.SendMessageResp;
import com.jiangjing.im.service.sequence.RedisSeq;
import com.jiangjing.im.service.utils.*;
import com.jiangjing.pack.message.ChatMessageAck;
import com.jiangjing.pack.message.MessageReceiveServerAckPack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 单聊消息处理类：消息发送法流程
 * 发送端 ----1、发送--------》 Chat Server  ---------》   接收端
 * 《---2、返回发送结果ack--        ----4、分发消息给各接收端
 * 《---3、同步给发送端的在线端----
 *
 * @author
 */
@Service
@Transactional
public class P2PMessageService {

    private static Logger logger = LoggerFactory.getLogger(P2PMessageService.class);

    @Autowired
    CheckSendMessageService checkSendMessageService;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    MessageStoreService messageStoreService;

    @Autowired
    RedisSeq redisSeq;

    @Autowired
    MessageCacheService messageCacheService;

    @Autowired
    AppConfig appConfig;

    @Autowired
    CallbackService callbackService;

    @Autowired
    AgentChatService agentChatService;

    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 消息的即时性做出的优化（同时多线程和 mq 的异步处理都会造成消息的无序性）
     * 1、前置校验（使用 dubbo 远程调用，将校验提前到 tcp 层，处理消息前优先校验）
     * 2、使用线程池提交任务处理消息，多线程
     * 3、在线程池持久化时，直接提交到 MQ 异步持久化
     * <p>
     * 消息有序性的保障：
     * 1、使用 Redis 的计数器  （目前采用的是 Redis）
     * 2、messageKey 是使用的雪花算法生成，也可以作为排序依据，但是会有时间回退的隐患
     * 3、使用其他第三方的序列服务。
     * <p>
     * 消息的可靠性：保证了可靠性的同时，因为重试机制就会引入幂等性的问题)
     * 1、使用服务的 ack （持久化消息后回复ack）和 接收端 ack （用户端接收端到消息回复ack，如果服务端不在线则由服务代为回复ack）两重确认机制确保消息送达
     * 2、在发送端没有接收到两个确认的 ack 前，根据重试机制重复发送消息。
     * <p>
     * 消息的幂等性保障：
     * 1、客户端在发送消息的会生成自己的 messageId，作为客户端自身信息的标识。
     * 2、在服务端，通过缓存，将 messageId 存入redis并设置过期时间，接收操消息是是否需要持久化需要判断缓存是否存在
     * 3、在接收端客户端，需要根据自身数据库判断接收到的消息的messageId，是否展示给用户
     *
     * @param messageContent
     */
    public void process(MessageContent messageContent) {

        // 1、判断消息缓存是否存在，存在则说明已经持久化，该消息是重发的消息，只需要再次触发消息的分发即可
        MessageContent messageIdCache = messageCacheService.getMessageFromMessageIdCache(messageContent.getAppId(), messageContent.getMessageId(), MessageContent.class);
        // 2、只需要重发，不需要的持久化
        if (messageIdCache != null) {
            ThreadPoolExecutorUtils.THREAD_POOL_EXECUTOR.execute(() -> {
                // 1、回复一个成功的 ack （消息保存成功，给发送端回复以一个ack）
                ack(messageContent, ResponseVO.successResponse());
                // 2、发消息给同步在线端
                syncToSender(messageIdCache, messageIdCache);
                // 3、发送给其他端(需要判断接收端是否全部不在线)
                List<ClientInfo> clientInfos = dispatchMessage(messageIdCache);
                if (clientInfos.isEmpty()) {
                    // 需要服务端代为回复 ack
                    receiverAck(messageIdCache);
                }
            });
            return;
        }

        // 3、发送消息前，回调业务接口，做第三方业务判断，校验失败直接回复相关的 ack
        if (appConfig.isSendMessageBeforeCallback()) {
            ResponseVO responseVO = callbackService.beforeCallback(messageContent.getAppId(), Constants.CallbackCommand.SEND_MESSAGE_BEFORE, JSON.toJSONString(messageContent));
            if (!responseVO.isOk()) {
                // 回复业务校验失败的 ack
                ack(messageContent, responseVO);
                return;
            }
        }


        // 获取消息序列,设置给消息自增序列，保证消息的有序性
        long seq = redisSeq.getSeq(messageContent.getAppId() + Constants.SeqConstants.MESSAGE_SEQ + ConversationIdGenerate.generateP2PId(messageContent.getFromId(), messageContent.getToId()));
        messageContent.setMessageSequence(seq);

        /*String fromId = messageContent.getFromId();
        String toId = messageContent.getToId();
        Integer appId = messageContent.getAppId();*/
        // 前置校验 -- （优化，判断消息的合法性，其实可以放在 TCP 层判断，不合法直接使用 noiServerSocketChannel 返回，不需要投递到mq）
      /*  ResponseVO responseVO = imServerPermissionCheck(fromId, toId, appId);
        if (responseVO.isOk()) {*/
        ThreadPoolExecutorUtils.THREAD_POOL_EXECUTOR.execute(() ->
        {
            /**
             * 优化二:数据库的操作是重型的，将需要插入的消息直接发送给 mq，由 mq 异步处理即可，不需要在这里实际操作数据，但是是需要确定好 messageKey
             */
            // 0、需要在消息回复之前将消息持久化，持久化消息不回丢失，才回复成功的 ack（不直接持久化，使用 mq 异步处理）
            messageStoreService.storeP2pMessage(messageContent);

            // 0-1、在回复 ack 之前做离线消息存储
            OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
            BeanUtils.copyProperties(messageContent, offlineMessageContent);
            offlineMessageContent.setConversationType(ConversationTypeEnum.P2P.getCode());
            offlineMessageContent.setDelFlag(DelFlagEnum.NORMAL.getCode());
            messageStoreService.storeOfflineMessage(offlineMessageContent);
            // 1、回复一个成功的 ack （消息保存成功，给发送端回复以一个ack）
            ack(messageContent, ResponseVO.successResponse());
            // 2、发消息给同步在线端
            syncToSender(messageContent, messageContent);
            // 3、发送给其他端
            List<ClientInfo> clientInfos = dispatchMessage(messageContent);
            // 4、消息处理结束添加缓存
            messageCacheService.setMessageFromMessageIdCache(messageContent.getAppId(), messageContent.getMessageId(), messageContent);
            if (clientInfos.isEmpty()) {
                receiverAck(messageContent);
            }
            // 只能对话
            callAgentChat(messageContent);
            // 5、发送完消息之后，回调业务接口
            if (appConfig.isSendMessageAfterCallback()) {
                callbackService.callback(messageContent.getAppId(), Constants.CallbackCommand.SEND_MESSAGE_AFTER, JSON.toJSONString(messageContent));
            }
            logger.info("消息处理完成：{}", messageContent.getMessageId());
        });
      /*  } else {
            // 如果校验失败，是需要给发送方端回复一个失败的 ack 的，NettyHandler 已经使用 dubbo 远程调用实现校验
            ack(messageContent, responseVO);
        }*/
    }

    /**
     * 发送消息前的前置校验
     * 1、用户双方自身你校验
     * 2、双方的好友关系校验
     *
     * @param fromId
     * @param toId
     * @param appId
     * @return
     */
    public ResponseVO imServerPermissionCheck(String fromId, String toId, Integer appId) {
        ResponseVO responseVO = checkSendMessageService.checkSenderFervidAndMute(fromId, appId);
        if (!responseVO.isOk()) {
            return responseVO;
        }
        responseVO = checkSendMessageService.checkFriendShip(fromId, toId, appId);
        return responseVO;
    }


    /**
     * 服务端持久化完消息之后回复 ack
     *
     * @param messageContent
     * @param responseVO
     */
    private void ack(MessageContent messageContent, ResponseVO responseVO) {
        logger.info("msg ack,msgId={},checkResult{}", messageContent.getMessageId(), responseVO.getCode());
        ChatMessageAck chatMessageAck = new
                ChatMessageAck(messageContent.getMessageId(), messageContent.getMessageSequence(), messageContent.getMessageKey(), messageContent.getToId());
        // 在给发送端回复ack时，携带消息本身的id信息，标识是那条消息的ack，前端方可操作
        responseVO.setData(chatMessageAck);
        // 回复一个 ack 给指定的端
        messageProducer.sendToUserByOne(messageContent.getFromId(), MessageCommand.MSG_ACK, responseVO, messageContent);
    }


    /**
     * 接收端不在线，服务端代为回复消息已经接收成功的 ack
     *
     * @param messageContent
     */
    public void receiverAck(MessageContent messageContent) {
        MessageReceiveServerAckPack serverAckPack = new MessageReceiveServerAckPack();
        serverAckPack.setMessageSequence(messageContent.getMessageSequence());
        serverAckPack.setMessageKey(messageContent.getMessageKey());
        serverAckPack.setToId(messageContent.getFromId());
        serverAckPack.setFromId(messageContent.getToId());
        serverAckPack.setServerSend(true);
        ClientInfo clientInfo = new ClientInfo();
        BeanUtils.copyProperties(messageContent, clientInfo);
        messageProducer.sendToUserByOne(messageContent.getFromId(), MessageCommand.MSG_RECEIVE_ACK, serverAckPack, clientInfo);
    }


    /**
     * 同步消息给其他在线端
     *
     * @param messageContent
     * @param clientInfo
     */
    private void syncToSender(MessageContent messageContent, ClientInfo clientInfo) {
        messageProducer.sendToUserExceptClient(messageContent.getFromId(), MessageCommand.MSG_P2P_SYNC, messageContent, clientInfo);
    }

    /**
     * 给接收放的所有在线端发送消息
     *
     * @param messageContent
     * @return
     */
    private List<ClientInfo> dispatchMessage(MessageContent messageContent) {
        return messageProducer.sendToUserByAll(messageContent.getToId(), messageContent.getAppId(), MessageCommand.MSG_P2P, messageContent);
    }


    public SendMessageResp sendMessage(SendMessageReq req) {
        MessageContent messageContent = new MessageContent();
        BeanUtils.copyProperties(req, messageContent);
        // 0、需要在消息回复之前将消息持久化，持久化消息不回丢失，才回复成功的 ack
        messageStoreService.storeP2pMessage(messageContent);
        // 2、发消息给同步在线端
        syncToSender(messageContent, messageContent);
        // 3、发送给其他端
        dispatchMessage(messageContent);
        // 4、设置返回结果
        SendMessageResp sendMessageResp = new SendMessageResp();
        sendMessageResp.setMessageKey(messageContent.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());
        return sendMessageResp;
    }

    /**
     * 接入智能对话，userId 为 324431782084609 作为智能对话端
     * @param messageContent
     */
    public void callAgentChat(MessageContent messageContent) {
        String toId = messageContent.getToId();
        if ("324431782084609".equals(toId)) {
            String content = JSON.parseObject(messageContent.getMessageBody(), MessageBody.class).getContent();
            Message userMsg = Message.builder().role(Role.USER.getValue()).content(content).build();
            MessageContent msgResult = new MessageContent();
            BeanUtils.copyProperties(messageContent, msgResult);
            msgResult.setFromId(toId);
            msgResult.setToId(messageContent.getFromId());
            msgResult.setMessageId(String.valueOf(snowflakeIdWorker.nextId()));
            // 智能对话，直接构造消息继续返回
            try {
                agentChatService.streamCallWithCallback(userMsg, (generationResult) -> {
                    MessageBody messageBody = new MessageBody(generationResult.getOutput().getChoices().get(0).getMessage().getContent());
                    msgResult.setMessageBody(JSONObject.from(messageBody).toJSONString());
                    dispatchMessage(msgResult);
                }, (stringBuilder) -> {
                    MessageBody messageBody = new MessageBody(stringBuilder.toString());
                    msgResult.setMessageBody(JSONObject.from(messageBody).toJSONString());
                    // 同步给智能对话的接收端
                    syncToSender(msgResult, new ClientInfo(messageContent.getAppId(), 99, ""));
                    // 持久化消息
                    messageStoreService.storeP2pMessage(msgResult);
                });
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}
