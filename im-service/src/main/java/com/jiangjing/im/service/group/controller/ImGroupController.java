package com.jiangjing.im.service.group.controller;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.model.SyncReq;
import com.jiangjing.im.service.group.model.req.*;
import com.jiangjing.im.service.group.service.ImGroupService;
import com.jiangjing.im.service.message.service.GroupMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jingjing
 * @date 2023/5/24 23:42
 */
@RestController
@RequestMapping("v1/group")
public class ImGroupController {

    @Autowired
    ImGroupService groupService;

    @Autowired
    GroupMessageService groupMessageService;


    /**
     * 第三方导入群组信息
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/importGroup")
    public ResponseVO importGroup(@RequestBody @Validated ImportGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return groupService.importGroup(req);
    }

    /**
     * 创建群
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/createGroup")
    public ResponseVO createGroup(@RequestBody @Validated CreateGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return groupService.createGroup(req);
    }


    /**
     * 获取群信息,包含群成员信息
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/getGroupInfo")
    public ResponseVO getGroupInfo(@RequestBody @Validated GetGroupReq req, Integer appId) {
        req.setAppId(appId);
        return groupService.getGroup(req);
    }

    /**
     * 更新群信息,需要校验当前人员的信息
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/update")
    public ResponseVO update(@RequestBody @Validated UpdateGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return groupService.updateBaseGroupInfo(req);
    }

    /**
     * 获取当前用户的加入的群组信息
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/getJoinedGroup")
    public ResponseVO getJoinedGroup(@RequestBody @Validated GetJoinedGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return groupService.getJoinedGroup(req);
    }

    /**
     * 解散群，支持群主或者后台管理员解散
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/destroyGroup")
    public ResponseVO destroyGroup(@RequestBody @Validated DestroyGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return groupService.destroyGroup(req);
    }

    /**
     * 转让群，只有群主才可以转让，并且被转让人需要在群内
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/transferGroup")
    public ResponseVO transferGroup(@RequestBody @Validated TransferGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return groupService.transferGroup(req);
    }

    /**
     * 禁言群
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/forbidSendMessage")
    public ResponseVO forbidSendMessage(@RequestBody @Validated MuteGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return groupService.muteGroup(req);
    }

    /**
     * 群聊消息发送接口
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/sendMessage")
    public ResponseVO sendMessage(@RequestBody @Validated SendGroupMessageReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return ResponseVO.successResponse(groupMessageService.sendMessage(req));
    }


    /**
     * 群列表增量同步
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/syncJoinedGroup")
    public ResponseVO syncJoinedGroup(@RequestBody @Validated SyncReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        return groupService.syncJoinedGroupList(req);
    }

}
