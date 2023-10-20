package com.jiangjing.im.message.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangjing.im.common.model.message.GroupChatMessageContent;
import com.jiangjing.im.common.model.message.MessageContent;
import com.jiangjing.im.message.dao.ImGroupMessageHistoryEntity;
import com.jiangjing.im.message.dao.ImMessageBodyEntity;
import com.jiangjing.im.message.dao.ImMessageHistoryEntity;
import com.jiangjing.im.message.dao.mapper.ImGroupMessageHistoryMapper;
import com.jiangjing.im.message.dao.mapper.ImMessageBodyMapper;
import com.jiangjing.im.message.dao.mapper.ImMessageHistoryMapper;
import com.jiangjing.im.message.model.DoStoreGroupMessageDto;
import com.jiangjing.im.message.model.DoStoreP2PMessageDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 持久化 message
 *
 * @author
 */
@Service
@Transactional
public class StoreMessageService extends ServiceImpl<ImMessageHistoryMapper, ImMessageHistoryEntity> implements InitializingBean {

    @Autowired
    ImMessageBodyMapper imMessageBodyMapper;

    @Autowired
    ImMessageHistoryMapper imMessageHistoryMapper;

    @Autowired
    ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;


    /**
     * 持久化单聊消息
     *
     * @param doStoreP2pMessageDto
     */
    public void doStoreP2PMessage(DoStoreP2PMessageDto doStoreP2pMessageDto) {
        MessageContent messageContent = doStoreP2pMessageDto.getMessageContent();
        ImMessageBodyEntity imMessageBodyEntity = doStoreP2pMessageDto.getMessageBody();
        // 插入 消息体
        imMessageBodyMapper.insert(imMessageBodyEntity);
        // 单聊消息是写扩散，所以需要生成各自的消息关系记录
        List<ImMessageHistoryEntity> historyEntityList = extractToP2pMessageHistory(messageContent, imMessageBodyEntity);
        // 批量插入消息关系
        saveBatch(historyEntityList);
    }


    private List<ImMessageHistoryEntity> extractToP2pMessageHistory(MessageContent messageContent, ImMessageBodyEntity imMessageBodyEntity) {
        ArrayList<ImMessageHistoryEntity> list = new ArrayList<>();
        ImMessageHistoryEntity historyEntityFrom = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, historyEntityFrom);
        historyEntityFrom.setOwnerId(messageContent.getFromId());
        historyEntityFrom.setMessageKey(imMessageBodyEntity.getMessageKey());
        historyEntityFrom.setCreateTime(System.currentTimeMillis());
        historyEntityFrom.setSequence(messageContent.getMessageSequence());
        list.add(historyEntityFrom);

        ImMessageHistoryEntity historyEntityTo = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, historyEntityTo);
        historyEntityTo.setOwnerId(messageContent.getToId());
        historyEntityTo.setMessageKey(imMessageBodyEntity.getMessageKey());
        historyEntityTo.setCreateTime(System.currentTimeMillis());
        historyEntityTo.setSequence(messageContent.getMessageSequence());
        list.add(historyEntityTo);
        return list;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet");
    }

    /**
     * 持久化群聊消息,采用 读扩散的方式
     *
     * @param doStoreGroupMessageDto
     */
    public void doStoreGroupMessage(DoStoreGroupMessageDto doStoreGroupMessageDto) {
        ImMessageBodyEntity imMessageBodyEntity = doStoreGroupMessageDto.getMessageBody();
        imMessageBodyMapper.insert(imMessageBodyEntity);
        // 转换 groupHistory 的记录信息
        ImGroupMessageHistoryEntity imGroupMessageHistoryEntity = extractToGroupMessageHistory(doStoreGroupMessageDto.getGroupChatMessageContent(), imMessageBodyEntity);
        imGroupMessageHistoryMapper.insert(imGroupMessageHistoryEntity);
    }

    private ImGroupMessageHistoryEntity extractToGroupMessageHistory(GroupChatMessageContent messageContent, ImMessageBodyEntity messageBodyEntity) {
        ImGroupMessageHistoryEntity result = new ImGroupMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, result);
        result.setGroupId(messageContent.getGroupId());
        result.setMessageKey(messageBodyEntity.getMessageKey());
        result.setCreateTime(System.currentTimeMillis());
        result.setSequence(messageContent.getMessageSequence());
        return result;
    }


}
