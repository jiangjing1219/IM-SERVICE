package com.jiangjing.im.service.friendship.service;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.jiangjing.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;

/**
 * @author jingjing
 * @date 2023/5/22 23:54
 */
public interface ImFriendShipGroupMemberService {

    /**
     * 添加分组好友信息
     *
     * @param addFriendShipGroupMemberReq
     */
    ResponseVO addGroupMember(AddFriendShipGroupMemberReq addFriendShipGroupMemberReq);

    /**
     * 物理删除该分组下的成员好友信息
     *
     * @param groupId
     */
    int clearGroupMember(Long groupId);

    /**
     * 删除分组好友信息
     *
     * @param req
     * @return
     */
    ResponseVO delGroupMember(DeleteFriendShipGroupMemberReq req);
}
