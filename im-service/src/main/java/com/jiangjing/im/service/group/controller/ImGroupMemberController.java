package com.jiangjing.im.service.group.controller;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.service.group.model.req.*;
import com.jiangjing.im.service.group.service.ImGroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jingjing
 * @date 2023/6/1 23:55
 */
@RestController
@RequestMapping("v1/group/member")
public class ImGroupMemberController {

    @Autowired
    private ImGroupMemberService imGroupMemberService;


    /**
     * 导入群成员
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/importGroupMember")
    public ResponseVO importGroupMember(@RequestBody @Validated ImportGroupMemberReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return imGroupMemberService.importGroupMember(req);
    }

    /**
     * 添加用户的到群组，拉人入群的逻辑，直接进入群聊。如果是后台管理员，则直接拉入群，
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/add")
    public ResponseVO addMember(@RequestBody @Validated AddGroupMemberReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return imGroupMemberService.addMember(req);
    }

    /**
     * 删除群成员
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/remove")
    public ResponseVO removeMember(@RequestBody @Validated RemoveGroupMemberReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return imGroupMemberService.removeMember(req);
    }

    /**
     * 更改群成员信息
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/update")
    public ResponseVO updateGroupMember(@RequestBody @Validated UpdateGroupMemberReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return imGroupMemberService.updateGroupMember(req);
    }

    /**
     * 设置禁言
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/speak")
    public ResponseVO speak(@RequestBody @Validated SpeaMemberReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return imGroupMemberService.speak(req);
    }
}
