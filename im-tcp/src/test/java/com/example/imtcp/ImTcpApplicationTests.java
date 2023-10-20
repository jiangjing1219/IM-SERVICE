package com.example.imtcp;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.dubbo.CheckMessageService;
import com.jiangjing.im.common.model.message.CheckSendMessageReq;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImTcpApplicationTests {

    @DubboReference(version = "1.0.0.0", group = "im")
    CheckMessageService checkMessageService;

    @Test
    void sendMessage() {
        CheckSendMessageReq checkSendMessageReq = new CheckSendMessageReq();
        checkSendMessageReq.setCommand(111);
        ResponseVO responseVO = checkMessageService.checkSendMessage(checkSendMessageReq);
        Assertions.assertEquals(responseVO.getCode(), 200);
    }

}
