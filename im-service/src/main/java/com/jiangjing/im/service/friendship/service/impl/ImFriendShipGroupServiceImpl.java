package com.jiangjing.im.service.friendship.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.DelFlagEnum;
import com.jiangjing.im.common.enums.FriendShipErrorCode;
import com.jiangjing.im.service.friendship.dao.ImFriendShipGroupEntity;
import com.jiangjing.im.service.friendship.dao.mapper.ImFriendShipGroupMapper;
import com.jiangjing.im.service.friendship.model.req.AddFriendShipGroupMemberReq;
import com.jiangjing.im.service.friendship.model.req.AddFriendShipGroupReq;
import com.jiangjing.im.service.friendship.model.req.DeleteFriendShipGroupReq;
import com.jiangjing.im.service.friendship.service.ImFriendShipGroupMemberService;
import com.jiangjing.im.service.friendship.service.ImFriendShipGroupService;
import com.jiangjing.im.service.sequence.RedisSeq;
import com.jiangjing.im.service.sequence.WriteUserSeq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jingjing
 * @date 2023/5/22 23:39
 */
@Service
@Transactional
public class ImFriendShipGroupServiceImpl implements ImFriendShipGroupService {

    @Autowired
    private ImFriendShipGroupMapper imFriendShipGroupMapper;

    @Autowired
    private ImFriendShipGroupMemberService imFriendShipGroupMemberService;

    @Autowired
    RedisSeq redisSeq;

    @Autowired
    WriteUserSeq writeUserSeq;


    /**
     * 添加分组
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO addGroup(AddFriendShipGroupReq req) {

        // 1、查询是否存在该分组
        QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", req.getGroupName());
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());
        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);

        if (entity != null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
        // 2、不存在，直接新增分组信息
        ImFriendShipGroupEntity insert = new ImFriendShipGroupEntity();
        long redisSeqSeq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.FRIENDSHIP_GROUP_SEQ);
        insert.setAppId(req.getAppId());
        insert.setCreateTime(System.currentTimeMillis());
        insert.setDelFlag(DelFlagEnum.NORMAL.getCode());
        insert.setGroupName(req.getGroupName());
        insert.setFromId(req.getFromId());
        insert.setSequence(redisSeqSeq);
        int insert1 = imFriendShipGroupMapper.insert(insert);
        if (insert1 != 1) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_CREATE_ERROR);
        }
        try {
            /**
             * 导入分组好友信息
             */
            if (CollectionUtil.isNotEmpty(req.getToIds())) {
                AddFriendShipGroupMemberReq addFriendShipGroupMemberReq = new AddFriendShipGroupMemberReq();
                addFriendShipGroupMemberReq.setFromId(req.getFromId());
                // 根据分组名称判断
                addFriendShipGroupMemberReq.setGroupName(req.getGroupName());
                addFriendShipGroupMemberReq.setToIds(req.getToIds());
                addFriendShipGroupMemberReq.setAppId(req.getAppId());
                imFriendShipGroupMemberService.addGroupMember(addFriendShipGroupMemberReq);
                return ResponseVO.successResponse();
            }
        } catch (DuplicateKeyException e) {
            e.getStackTrace();
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
        // 更新缓存
        writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FRIENDSHIP_GROUP_SEQ, redisSeqSeq);
        return ResponseVO.successResponse();
    }

    /**
     * 获取好友分组信息
     *
     * @param fromId
     * @param groupName
     * @param appId
     * @return
     */
    @Override
    public ResponseVO getGroup(String fromId, String groupName, Integer appId) {
        QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", groupName);
        query.eq("app_id", appId);
        query.eq("from_id", fromId);
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());
        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);
        if (entity != null) {
            return ResponseVO.successResponse(entity);
        }
        return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
    }

    /**
     * 删除分组信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO deleteGroup(DeleteFriendShipGroupReq req) {
        // 1、查询分组信息是否存在
        long redisSeqSeq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.FRIENDSHIP_GROUP_SEQ);
        req.getGroupName().forEach(groupName -> {
            QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
            query.eq("group_name", groupName);
            query.eq("app_id", req.getAppId());
            query.eq("from_id", req.getFromId());
            query.eq("del_flag", DelFlagEnum.NORMAL.getCode());
            ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);
            if (entity != null) {
                ImFriendShipGroupEntity update = new ImFriendShipGroupEntity();
                update.setGroupId(entity.getGroupId());
                update.setDelFlag(DelFlagEnum.DELETE.getCode());
                update.setSequence(redisSeqSeq);
                // 更新删除表示
                imFriendShipGroupMapper.updateById(update);
                // 物理删除成员好友数据
                imFriendShipGroupMemberService.clearGroupMember(entity.getGroupId());
            }
        });
        // 更新缓存
        writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FRIENDSHIP_GROUP_SEQ, redisSeqSeq);
        return ResponseVO.successResponse();
    }
}
