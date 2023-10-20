package com.jiangjing.im.service.friendship.controller;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.jiangjing.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.jiangjing.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.jiangjing.im.service.friendship.model.req.DeleteFriendShipGroupReq;
import com.jiangjing.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.jiangjing.im.service.friendship.service.ImFriendShipGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jingjing
 * @date 2023/5/22 23:37
 */
@RestController
@RequestMapping("v1/friendship/group")
public class ImFriendShipGroupController {

    @Autowired
    ImFriendShipGroupService imFriendShipGroupService;

    @Autowired
    ImFriendShipGroupMemberService imFriendShipGroupMemberService;


    /**
     * 添加好友信息,支持在分组的同时导入分组好友信息
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/add")
    public ResponseVO add(@RequestBody @Validated AddFriendShipGroupReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipGroupService.addGroup(req);
    }


    /**
     * 删除分组信息，根据分组名称去删除
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/del")
    public ResponseVO del(@RequestBody @Validated DeleteFriendShipGroupReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipGroupService.deleteGroup(req);
    }

    /**
     * 添加分组好友信息
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/member/add")
    public ResponseVO memberAdd(@RequestBody @Validated AddFriendShipGroupMemberReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipGroupMemberService.addGroupMember(req);
    }

    /**
     * 删除分组好友信息
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/member/del")
    public ResponseVO memberdel(@RequestBody @Validated DeleteFriendShipGroupMemberReq req, Integer appId)  {
        req.setAppId(appId);
        return imFriendShipGroupMemberService.delGroupMember(req);
    }
}
