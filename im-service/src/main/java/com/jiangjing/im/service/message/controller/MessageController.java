package com.jiangjing.im.service.message.controller;


import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.model.SyncReq;
import com.jiangjing.im.service.message.model.req.GroupMessageHistoryReq;
import com.jiangjing.im.service.message.model.req.P2pMessageHistoryReq;
import com.jiangjing.im.service.message.model.req.SendMessageReq;
import com.jiangjing.im.service.message.model.resp.GroupMessageHistoryResp;
import com.jiangjing.im.service.message.model.resp.P2pMessageHistoryResp;
import com.jiangjing.im.service.message.service.MessageSyncService;
import com.jiangjing.im.service.message.service.P2PMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Admin
 */
@RestController
@RequestMapping("v1/message")
public class MessageController {

    @Autowired
    P2PMessageService p2PMessageService;

    @Autowired
    MessageSyncService messageSyncService;


    /**
     * 单聊消息的发送接口
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/send")
    public ResponseVO send(@RequestBody @Validated SendMessageReq req, Integer appId) {
        req.setAppId(appId);
        return ResponseVO.successResponse(p2PMessageService.sendMessage(req));
    }


    /**
     * 同步离线消息
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/syncOfflineMessage")
    public ResponseVO syncOfflineMessage(@RequestBody @Validated SyncReq req, Integer appId) {
        req.setAppId(appId);
        return messageSyncService.syncOfflineMessage(req);
    }


    /**
     * 查询单聊聊天历史
     *
     * @param req
     * @return
     */
    @RequestMapping("/queryP2pMessageHistory")
    public ResponseVO queryP2pMessageHistory(@RequestBody @Validated P2pMessageHistoryReq req) {
        List<P2pMessageHistoryResp> messageHistory = messageSyncService.queryP2pMessageHistory(req);
        return ResponseVO.successResponse(messageHistory);
    }

    /**
     * 查询群聊聊天历史
     *
     * @param req
     * @return
     */
    @RequestMapping("/queryGroupMessageHistory")
    public ResponseVO queryGroupMessageHistory(@RequestBody @Validated GroupMessageHistoryReq req) {
        List<GroupMessageHistoryResp> messageHistory = messageSyncService.queryGroupMessageHistory(req);
        return ResponseVO.successResponse(messageHistory);
    }

}
