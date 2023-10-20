package com.jiangjing.im.service.conversation.controller;


import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.model.SyncReq;
import com.jiangjing.im.service.conversation.model.DeleteConversationReq;
import com.jiangjing.im.service.conversation.model.UpdateConversationReq;
import com.jiangjing.im.service.conversation.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会话接口
 *
 * @author
 */
@RestController
@RequestMapping("v1/conversation")
public class ConversationController {

    @Autowired
    ConversationService conversationService;

    /**
     * 删除会话
     * 1、App端本地删除，但是对于服务端来说是不做任何操作
     * 2、根据配置判断是否需要多端同步，其他端同步删除
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/deleteConversation")
    public ResponseVO deleteConversation(@RequestBody @Validated DeleteConversationReq req, Integer appId) {
        req.setAppId(appId);
        return conversationService.deleteConversation(req);
    }

    /**
     * 更新会话： 顶置/免打扰
     * 1、更新 isTop / isMute
     * 2、同步到其他端
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/updateConversation")
    public ResponseVO updateConversation(@RequestBody @Validated UpdateConversationReq req, Integer appId) {
        req.setAppId(appId);
        return conversationService.updateConversation(req);
    }



    /**
     * 会话同步（使用 sequence 实现增量同步）
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/syncConversationSet")
    public ResponseVO syncConversationSet(@RequestBody @Validated SyncReq req, Integer appId){
        req.setAppId(appId);
        return conversationService.syncConversationSet(req);
    }

}
