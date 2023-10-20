package com.jiangjing.im.service.group.service;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.service.group.model.req.*;
import com.jiangjing.im.service.group.model.resp.GetRoleInGroupResp;

import java.util.Collection;
import java.util.List;

/**
 * @author jingjing
 * @date 2023/5/25 0:19
 */
public interface ImGroupMemberService {
    /**
     * 新增群成员
     *
     * @param groupId        群主键id
     * @param appId          appId
     * @param groupMemberDto 群成员信息
     */
    ResponseVO addGroupMember(String groupId, Integer appId, GroupMemberDto groupMemberDto);

    /**
     * 获取群成员信息
     *
     * @param groupId
     * @param appId
     * @return
     */
    ResponseVO<List<GroupMemberDto>> getGroupMember(String groupId, Integer appId);

    /**
     * 获取当前群成员的 userID
     *
     * @param groupId
     * @param appId
     * @return
     */
    List<String> getGroupMemberIds(String groupId, Integer appId);

    /**
     * 获取群管理员
     *
     * @param groupId
     * @param appId
     * @return
     */
    List<GroupMemberDto> getGroupManagers(String groupId, Integer appId);

    /**
     * 查询当前群成员的信息
     *
     * @param groupId
     * @param memberId
     * @param appId
     * @return
     */
    ResponseVO<GetRoleInGroupResp> getRoleInGroupOne(String groupId, String memberId, Integer appId);

    /**
     * 获取的当前用户的加入的群id集合
     *
     * @param req
     * @return
     */
    ResponseVO<Collection<String>> getMemberJoinedGroup(GetJoinedGroupReq req);

    /**
     * 导入群成员信息
     *
     * @param req
     * @return
     */
    ResponseVO importGroupMember(ImportGroupMemberReq req);

    /**
     * 批量拉入用户进群
     *
     * @param req
     * @return
     */
    ResponseVO addMember(AddGroupMemberReq req);

    /**
     * 删除群成员
     *
     * @param req
     * @return
     */
    ResponseVO removeMember(RemoveGroupMemberReq req);

    /**
     * 更改群成员信息
     *
     * @param req
     * @return
     */
    ResponseVO updateGroupMember(UpdateGroupMemberReq req);

    /**
     * 设置禁言
     *
     * @param req
     * @return
     */
    ResponseVO speak(SpeaMemberReq req);

    /**
     * 转让群主，更新角色信息
     *
     * @param ownerId
     * @param groupId
     * @param appId
     */
    ResponseVO transferGroupMember(String ownerId, String groupId, Integer appId);

    /**
     * 获取执行用户加入的群组，排除执行角色
     *
     * @param appId
     * @param userId
     * @param excludeRole
     * @return
     */
    List<String> getMemberJoinedGroupByCondition(Integer appId, String userId, int excludeRole);
}
