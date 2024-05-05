package com.jiangjing.im.service.message.service.dubbo;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.dubbo.CheckMessageService;
import com.jiangjing.im.common.enums.command.GroupEventCommand;
import com.jiangjing.im.common.enums.command.MessageCommand;
import com.jiangjing.im.common.model.message.CheckSendMessageReq;
import com.jiangjing.im.service.message.service.CheckSendMessageService;
import com.jiangjing.im.service.message.service.P2PMessageService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Admin
 */
@DubboService(version = "1.0.0.0", group = "im")
public class CheckMessageServiceImpl implements CheckMessageService {

    @Autowired
    P2PMessageService p2PMessageService;

    @Autowired
    CheckSendMessageService checkSendMessageService;

    /**
     * 暴露给Dubbo服务调用，检查发送消息时的前置校验
     *
     * @param checkSendMessageReq 请求参数（单聊消息/群组消息）
     * @return
     */
    @Override
    public ResponseVO checkSendMessage(CheckSendMessageReq checkSendMessageReq) {
        // 单聊前置校验
        if (checkSendMessageReq.getCommand() == MessageCommand.MSG_P2P.getCommand()) {
            return p2PMessageService.imServerPermissionCheck(checkSendMessageReq.getFromId(), checkSendMessageReq.getToId(), checkSendMessageReq.getAppId());
            // 群聊前置校验
        } else if (checkSendMessageReq.getCommand() == GroupEventCommand.MSG_GROUP.getCommand()) {
            return checkSendMessageService.checkGroupMessage(checkSendMessageReq.getFromId(), checkSendMessageReq.getGroupId(), checkSendMessageReq.getAppId());
        }
        return ResponseVO.successResponse();
    }
}
