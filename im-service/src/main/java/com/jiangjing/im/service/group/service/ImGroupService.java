package com.jiangjing.im.service.group.service;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.model.SyncReq;
import com.jiangjing.im.service.group.model.req.*;

/**
 * @author jingjing
 * @date 2023/5/24 23:52
 */
public interface ImGroupService {

    /**
     * 第三方群信息
     *
     * @param req
     * @return
     */
    ResponseVO importGroup(ImportGroupReq req);

    /**
     * 创建群
     *
     * @param req
     * @return
     */
    ResponseVO createGroup(CreateGroupReq req);

    /**
     * 获取指定的群消息
     *
     * @param req
     * @return
     */
    ResponseVO getGroup(GetGroupReq req);

    /**
     * 获取指定的群信息
     *
     * @param groupId
     * @param appId
     * @return
     */
    ResponseVO getGroup(String groupId, Integer appId);

    /**
     * 更新群信息
     *
     * @param req
     * @return
     */
    ResponseVO updateBaseGroupInfo(UpdateGroupReq req);

    /**
     * 获取当前用户的加入的群信息
     *
     * @param req
     * @return
     */
    ResponseVO getJoinedGroup(GetJoinedGroupReq req);

    /**
     * 解散群，支持群主或者后台管理员解散
     *
     * @param req
     * @return
     */
    ResponseVO destroyGroup(DestroyGroupReq req);

    /**
     * 转让群
     *
     * @param req
     * @return
     */
    ResponseVO transferGroup(TransferGroupReq req);

    /**
     * 禁言群
     *
     * @param req
     * @return
     */
    ResponseVO muteGroup(MuteGroupReq req);

    /**
     * 群列表增量同步
     *
     * @param req
     * @return
     */
    ResponseVO syncJoinedGroupList(SyncReq req);

    /**
     * 获取当前用户群组最大的seq
     *
     * @param appId
     * @param userId
     * @return
     */
    Long getMaxUserGroupSeq(Integer appId, String userId);
}
