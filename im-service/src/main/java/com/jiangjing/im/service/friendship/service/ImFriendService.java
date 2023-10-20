package com.jiangjing.im.service.friendship.service;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.model.RequestBase;
import com.jiangjing.im.common.model.SyncReq;
import com.jiangjing.im.service.friendship.model.req.*;

import java.util.List;

/**
 * @author jingjing
 * @date 2023/5/3 23:23
 */
public interface ImFriendService {
    /**
     * 批量导入用户还有信息
     *
     * @param req
     * @return
     */
    ResponseVO importFriendShip(ImporFriendShipReq req);

    /**
     * 添加好友，一对一
     *
     * @param req
     * @return
     */
    ResponseVO addFriend(AddFriendReq req);

    /**
     *  添加好友
     * @param req
     * @param fromId
     * @param toItem
     * @param appId
     * @return
     */
    ResponseVO doAddFriend(RequestBase req, String fromId, FriendDto toItem, Integer appId);

    /**
     * 更新好友信息
     *
     * @param req
     * @return
     */
    ResponseVO updateFriend(UpdateFriendReq req);

    /**
     * 删除指定的好友
     *
     * @param req
     * @return
     */
    ResponseVO deleteFriend(DeleteFriendReq req);

    /**
     * 删除指定用户所有的好友
     *
     * @param req
     * @return
     */
    ResponseVO deleteAllFriend(DeleteFriendReq req);

    /**
     *  获取当前用户的所有的好友信息
     *
     * @param req
     * @return
     */
    ResponseVO getAllFriendShip(GetAllFriendShipReq req);

    /**
     *  获取指定好友的好友信息
     *
     * @param req
     * @return
     */
    ResponseVO getRelation(GetRelationReq req);

    /**
     *  校验好友状态
     *
     * @param req
     * @return
     */
    ResponseVO checkFriendship(CheckFriendShipReq req);

    /**
     *  拉黑
     *
     * @param req
     * @return
     */
    ResponseVO addBlack(AddFriendShipBlackReq req);

    /**
     *  拉黑 -》 正常
     *
     * @param req
     * @return
     */
    ResponseVO deleteBlack(DeleteBlackReq req);

    /***
     *  单向校验来黑状态
     * @param req
     * @return
     */
    ResponseVO checkBlack(CheckFriendShipReq req);

    /**
     * 同步好友关系列表
     *
     * @param req
     * @return
     */
    ResponseVO syncFriendshipList(SyncReq req);

    /**
     * 获取当前用户的所有的好友 id
     *
     * @param appId
     * @param userId
     * @return
     */
    List<String> getAllFriendId(Integer appId, String userId);
}
