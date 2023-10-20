package com.jiangjing.im.service.friendship.service;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.service.friendship.model.req.ApproverFriendRequestReq;
import com.jiangjing.im.service.friendship.model.req.FriendDto;
import com.jiangjing.im.service.friendship.model.req.ReadFriendShipRequestReq;

/**
 * 好友申请 Service
 *
 * @author jingjing
 * @date 2023/5/11 23:49
 */
public interface ImFriendShipRequestService {

    /**
     *  添加好友申请
     * @param fromId
     * @param dto
     * @param appId
     * @return
     */
    ResponseVO addFriendshipRequest(String fromId, FriendDto dto, Integer appId);

    /**
     * 根据好友申请记录的主键，审核好友申请
     *
     * @param req
     * @return
     */
    ResponseVO approveFriendRequest(ApproverFriendRequestReq req);

    /**
     * 获取当前用户的所有的好友申请
     *
     * @param fromId
     * @param appId
     * @return
     */
    ResponseVO getFriendRequest(String fromId, Integer appId);

    /**
     * 已读 好友申请
     *
     * @param req
     * @return
     */
    ResponseVO readFriendShipRequestReq(ReadFriendShipRequestReq req);
}
