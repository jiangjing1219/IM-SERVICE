package com.jiangjing.im.service.friendship.service;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.jiangjing.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.jiangjing.im.service.friendship.model.req.DeleteFriendShipGroupReq;

/**
 * @author jingjing
 * @date 2023/5/22 23:39
 */
public interface ImFriendShipGroupService {

    /**
     * 添加分组信息
     *
     * @param req
     * @return
     */
    ResponseVO addGroup(AddFriendShipGroupReq req);

    /**
     * 获取分组信息
     *
     * @param fromId
     * @param groupName
     * @param appId
     * @return
     */
    ResponseVO<ImFriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId);

    /**
     * 删除分组信息
     *
     * @param req
     * @return
     */
    ResponseVO deleteGroup(DeleteFriendShipGroupReq req);
}
