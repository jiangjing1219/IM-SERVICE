package com.jiangjing.im;

import com.jiangjing.im.common.model.message.MessageContent;
import com.jiangjing.im.message.dao.ImMessageBodyEntity;
import com.jiangjing.im.message.model.DoStoreP2PMessageDto;
import com.jiangjing.im.message.service.StoreMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImMessageStoreApplicationTests {

    @Autowired
    StoreMessageService storeMessageService;

    @Test
    void contextLoads() {
        DoStoreP2PMessageDto doStoreP2PMessageDto = new DoStoreP2PMessageDto();
        ImMessageBodyEntity imMessageBodyEntity = new ImMessageBodyEntity();
        MessageContent messageContent = new MessageContent();
        messageContent.setMessageKey(100001L);
        messageContent.setAppId(10000);
        messageContent.setMessageBody("你好呀");
        messageContent.setMessageTime(System.currentTimeMillis());
        messageContent.setFromId("0001");
        messageContent.setToId("002");
        BeanUtils.copyProperties(messageContent,imMessageBodyEntity);
        doStoreP2PMessageDto.setMessageContent(messageContent);
        doStoreP2PMessageDto.setImMessageBodyEntity(imMessageBodyEntity);
        storeMessageService.doStoreP2PMessage(doStoreP2PMessageDto);
    }
}
