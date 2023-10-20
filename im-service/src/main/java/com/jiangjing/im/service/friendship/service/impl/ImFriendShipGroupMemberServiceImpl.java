package com.jiangjing.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.jiangjing.im.service.friendship.dao.ImFriendShipGroupMemberEntity;
import com.jiangjing.im.service.friendship.dao.mapper.ImFriendShipGroupMemberMapper;
import com.jiangjing.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.jiangjing.im.service.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.jiangjing.im.service.friendship.model.resp.ImportFriendShipGroupMemberResp;
import com.jiangjing.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.jiangjing.im.service.friendship.service.ImFriendShipGroupService;
import com.jiangjing.im.service.sequence.RedisSeq;
import com.jiangjing.im.service.sequence.WriteUserSeq;
import com.jiangjing.im.service.user.dao.ImUserDataEntity;
import com.jiangjing.im.service.user.service.ImUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jingjing
 * @date 2023/5/22 23:54
 */
@Service
@Transactional
public class ImFriendShipGroupMemberServiceImpl implements ImFriendShipGroupMemberService {

    @Autowired
    private ImFriendShipGroupService imFriendShipGroupService;

    @Autowired
    private ImUserService imUserService;

    @Autowired
    private ImFriendShipGroupMemberMapper imFriendShipGroupMemberMapper;

    @Autowired
    RedisSeq redisSeq;

    @Autowired
    WriteUserSeq writeUserSeq;

    /**
     * 添加分组好友信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO addGroupMember(AddFriendShipGroupMemberReq req) {

        // 1、根据用户id、分组名称、appid 获取相应的分组信息
        ResponseVO<ImFriendShipGroupEntity> group = imFriendShipGroupService.getGroup(req.getFromId(), req.getGroupName(), req.getAppId());
        if (!group.isOk()) {
            return group;
        }

        // 2、插入好友信息
        List<String> successId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            if (singleUserInfo.isOk()) {
                int i = doAddGroupMember(group.getData().getGroupId(), toId);
                if (i == 1) {
                    successId.add(toId);
                }
            }
        }
        ImportFriendShipGroupMemberResp importFriendShipGroupMemberResp = new ImportFriendShipGroupMemberResp();
        importFriendShipGroupMemberResp.setSuccessId(successId);
        return ResponseVO.successResponse(importFriendShipGroupMemberResp);
    }

    /**
     * 物理删除成员好友信息（全部删除）
     *
     * @param groupId
     */
    @Override
    public int clearGroupMember(Long groupId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> query = new QueryWrapper<>();
        query.eq("group_id", groupId);
        int delete = imFriendShipGroupMemberMapper.delete(query);
        return delete;
    }

    @Override
    public ResponseVO delGroupMember(DeleteFriendShipGroupMemberReq req) {
        // 1、查询该分组是否存在
        ResponseVO<ImFriendShipGroupEntity> group = imFriendShipGroupService.getGroup(req.getFromId(), req.getGroupName(), req.getAppId());
        if (!group.isOk()) {
            return group;
        }

        // 2、遍历删除分组好友
        for (String toId : req.getToIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            if (singleUserInfo.isOk()) {
                int i = deleteGroupMember(group.getData().getGroupId(), req.getToIds());
            }
        }
        return ResponseVO.successResponse();
    }

    /**
     * 删除指定的好友信息（部分删除）
     *
     * @param groupId
     * @param toIds
     * @return
     */
    private int deleteGroupMember(Long groupId, List<String> toIds) {
        QueryWrapper<ImFriendShipGroupMemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        queryWrapper.in("to_id", toIds);
        try {
            int delete = imFriendShipGroupMemberMapper.delete(queryWrapper);
            return delete;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 添加分组好友信息
     *
     * @param groupId
     * @param toId
     * @return
     */
    private int doAddGroupMember(Long groupId, String toId) {
        ImFriendShipGroupMemberEntity imFriendShipGroupMemberEntity = new ImFriendShipGroupMemberEntity();
        imFriendShipGroupMemberEntity.setGroupId(groupId);
        imFriendShipGroupMemberEntity.setToId(toId);
        try {
            int insert = imFriendShipGroupMemberMapper.insert(imFriendShipGroupMemberEntity);
            return insert;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
