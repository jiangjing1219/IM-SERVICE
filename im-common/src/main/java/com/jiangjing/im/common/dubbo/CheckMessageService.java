package com.jiangjing.im.common.dubbo;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.model.message.CheckSendMessageReq;

/**
 * dubbo 远程调用的接口
 *
 * @author
 */
public interface CheckMessageService {

    /**
     * 消息发送前的前置校验
     *
     * @param checkSendMessageReq 请求参数（单聊消息/群组消息）
     * @return
     */
    public ResponseVO checkSendMessage(CheckSendMessageReq checkSendMessageReq);
}
