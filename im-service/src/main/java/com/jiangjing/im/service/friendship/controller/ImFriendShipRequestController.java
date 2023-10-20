package com.jiangjing.im.service.friendship.controller;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.service.friendship.model.req.ApproverFriendRequestReq;
import com.jiangjing.im.service.friendship.model.req.GetFriendShipRequestReq;
import com.jiangjing.im.service.friendship.model.req.ReadFriendShipRequestReq;
import com.jiangjing.im.service.friendship.service.ImFriendShipRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jingjing
 * @date 2023/5/12 0:19
 */
@RestController
@RequestMapping("v1/friendshipRequest")
public class ImFriendShipRequestController {

    @Autowired
    ImFriendShipRequestService imFriendShipRequestService;

    /**
     * 好友申请审核接口
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/approveFriendRequest")
    public ResponseVO approveFriendRequest(@RequestBody @Validated
                                           ApproverFriendRequestReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return imFriendShipRequestService.approveFriendRequest(req);
    }

    /**
     * 获取指定用户的所有的好友申请
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/getFriendRequest")
    public ResponseVO getFriendRequest(@RequestBody @Validated GetFriendShipRequestReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipRequestService.getFriendRequest(req.getFromId(), req.getAppId());
    }

    /**
     * 已读 好友申请
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/readFriendShipRequestReq")
    public ResponseVO readFriendShipRequestReq(@RequestBody @Validated ReadFriendShipRequestReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipRequestService.readFriendShipRequestReq(req);
    }


}
