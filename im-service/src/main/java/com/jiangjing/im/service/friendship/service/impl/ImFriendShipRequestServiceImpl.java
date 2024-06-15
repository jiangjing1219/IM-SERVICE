package com.jiangjing.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.ApproverFriendRequestStatusEnum;
import com.jiangjing.im.common.enums.FriendShipErrorCode;
import com.jiangjing.im.common.enums.command.FriendshipEventCommand;
import com.jiangjing.im.common.exception.ApplicationException;
import com.jiangjing.im.common.model.ClientInfo;
import com.jiangjing.im.service.friendship.dao.ImFriendShipRequestEntity;
import com.jiangjing.im.service.friendship.dao.mapper.ImFriendShipRequestMapper;
import com.jiangjing.im.service.friendship.model.req.ApproverFriendRequestReq;
import com.jiangjing.im.service.friendship.model.req.FriendDto;
import com.jiangjing.im.service.friendship.model.req.ReadFriendShipRequestReq;
import com.jiangjing.im.service.friendship.service.ImFriendService;
import com.jiangjing.im.service.friendship.service.ImFriendShipRequestService;
import com.jiangjing.im.service.sequence.RedisSeq;
import com.jiangjing.im.service.sequence.WriteUserSeq;
import com.jiangjing.im.service.utils.MessageProducer;
import com.jiangjing.pack.friendship.ApproverFriendRequestPack;
import com.jiangjing.pack.friendship.ReadAllFriendRequestPack;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author jingjing
 * @date 2023/5/11 23:50
 */
@Transactional
@Service
public class ImFriendShipRequestServiceImpl implements ImFriendShipRequestService {

    @Autowired
    private ImFriendShipRequestMapper imFriendShipRequestMapper;

    @Autowired
    private ImFriendService imFriendService;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    RedisSeq redisSeq;

    @Autowired
    WriteUserSeq writeUserSeq;


    /**
     * 添加好友申请
     *
     * @param fromId
     * @param dto
     * @param appId
     * @return
     */
    @Override
    public ResponseVO addFriendshipRequest(String fromId, FriendDto dto, Integer appId) {
        // 1、查询是否存在好友申请
        QueryWrapper<ImFriendShipRequestEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId);
        queryWrapper.eq("from_id", fromId);
        queryWrapper.eq("to_id", dto.getToId());
        ImFriendShipRequestEntity request = imFriendShipRequestMapper.selectOne(queryWrapper);
        // 好后申请序列
        long redisSeqSeq = redisSeq.getSeq(appId + ":" + Constants.SeqConstants.FRIENDSHIP_REQUEST_SEQ);
        // 2、不存在，则需新增一条好友申请，或者是直接已经【已拒绝】
        if (request == null || request.getApproveStatus() == 2) {
            request = new ImFriendShipRequestEntity();
            request.setAddSource(dto.getAddSource());
            request.setAddWording(dto.getAddWording());
            request.setAppId(appId);
            request.setFromId(fromId);
            request.setToId(dto.getToId());
            request.setReadStatus(0);
            request.setApproveStatus(0);
            request.setRemark(dto.getRemark());
            request.setSequence(redisSeqSeq);
            request.setCreateTime(System.currentTimeMillis());
            request.setUpdateTime(request.getCreateTime());
            imFriendShipRequestMapper.insert(request);
        } else {
            // 3、如果是已经存在的好友申请，那么直接需要更新申请里面的记录即可
            //修改记录内容 和更新时间
            if (StringUtils.isNotBlank(dto.getAddSource())) {
                request.setAddWording(dto.getAddWording());
            }
            if (StringUtils.isNotBlank(dto.getRemark())) {
                request.setRemark(dto.getRemark());
            }
            if (StringUtils.isNotBlank(dto.getAddWording())) {
                request.setAddWording(dto.getAddWording());
            }
            // 4、读取状态重新变更为未读
            request.setReadStatus(0);
            request.setSequence(redisSeqSeq);
            imFriendShipRequestMapper.updateById(request);
        }
        // 更新缓存
        writeUserSeq.writeUserSeq(appId, dto.getToId(), Constants.SeqConstants.FRIENDSHIP_REQUEST_SEQ, redisSeqSeq);
        // 5、添加完好友申请之后需要发送好友申请到客户端，被添加的用户出现好友申请
        messageProducer.sendToUserByAll(dto.getToId(), request.getAppId(), FriendshipEventCommand.FRIEND_REQUEST, request);
        return ResponseVO.successResponse();
    }

    /**
     * 审核好友申请
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO approveFriendRequest(ApproverFriendRequestReq req) {
        // 1、根据主键查询好友申请记录
        ImFriendShipRequestEntity imFriendShipRequestEntity = imFriendShipRequestMapper.selectById(req.getId());
        if (imFriendShipRequestEntity == null) {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_REQUEST_IS_NOT_EXIST);
        }

        if (!req.getOperate().equals(imFriendShipRequestEntity.getToId())) {
            //只能审批发给自己的好友请求
            throw new ApplicationException(FriendShipErrorCode.NOT_APPROVER_OTHER_MAN_REQUEST);
        }
        long redisSeqSeq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.FRIENDSHIP_REQUEST_SEQ);
        ImFriendShipRequestEntity update = new ImFriendShipRequestEntity();
        update.setApproveStatus(req.getStatus());
        update.setUpdateTime(System.currentTimeMillis());
        update.setId(req.getId());
        update.setSequence(redisSeqSeq);
        // 2、更新申请记录的信息
        imFriendShipRequestMapper.updateById(update);
        // 更新缓存
        writeUserSeq.writeUserSeq(req.getAppId(), req.getOperate(), Constants.SeqConstants.FRIENDSHIP_REQUEST_SEQ, redisSeqSeq);

        // 3、如果是审核通过，那么就添加好友信息
        if (ApproverFriendRequestStatusEnum.AGREE.getCode() == req.getStatus()) {
            FriendDto dto = new FriendDto();
            dto.setAddSource(imFriendShipRequestEntity.getAddSource());
            dto.setAddWording(imFriendShipRequestEntity.getAddWording());
            dto.setRemark(imFriendShipRequestEntity.getRemark());
            dto.setToId(imFriendShipRequestEntity.getToId());
            ResponseVO responseVO = imFriendService.doAddFriend(req, imFriendShipRequestEntity.getFromId(), dto, req.getAppId());
            if (!responseVO.isOk()) {
                return responseVO;
            }
        }

        // 好友审核通过，需要给申请方，发送申请结果
        ApproverFriendRequestPack approverFriendRequestPack = new ApproverFriendRequestPack();
        approverFriendRequestPack.setId(req.getId());
        approverFriendRequestPack.setStatus(req.getStatus());
        // fromId 是申请的发送方，但是申请记录是该 toId 接受方看到，所以也是 toId 接收方做的审核，需要多端同步的也是接收方
        messageProducer.sendToUserByAll(imFriendShipRequestEntity.getToId(), req.getAppId(), FriendshipEventCommand.FRIEND_REQUEST_APPROVER, approverFriendRequestPack);
        return ResponseVO.successResponse();
    }

    /**
     * 获取指定用户的所有的好友申请记录
     *
     * @param fromId
     * @param appId
     * @return
     */
    @Override
    public ResponseVO getFriendRequest(String fromId, Integer appId) {
        QueryWrapper<ImFriendShipRequestEntity> mapper = new QueryWrapper<>();
        mapper.eq("to_id", fromId);
        mapper.eq("app_id", appId);
        // 未读的
        mapper.eq("read_status", 0);
        List<ImFriendShipRequestEntity> requestList = imFriendShipRequestMapper.selectList(mapper);
        return ResponseVO.successResponse(requestList);
    }

    /**
     * 已读好友申请（一次读取全部）
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO readFriendShipRequestReq(ReadFriendShipRequestReq req) {
        QueryWrapper<ImFriendShipRequestEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("to_id", req.getFromId());
        long redisSeqSeq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.FRIENDSHIP_REQUEST_SEQ);
        ImFriendShipRequestEntity update = new ImFriendShipRequestEntity();
        update.setSequence(redisSeqSeq);
        update.setReadStatus(1);
        imFriendShipRequestMapper.update(update, query);

        // 申请记录已读，需要同步到当前用户的其他端
        ReadAllFriendRequestPack readAllFriendRequestPack = new ReadAllFriendRequestPack();
        readAllFriendRequestPack.setFromId(req.getFromId());
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setClientType(req.getClientType());
        clientInfo.setImei(req.getImei());
        clientInfo.setAppId(req.getAppId());
        // 更新缓存
        writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FRIENDSHIP_REQUEST_SEQ, redisSeqSeq);
        // 同步给其他客户端
        messageProducer.sendToUserExceptClient(req.getFromId(), FriendshipEventCommand.FRIEND_REQUEST_READ, readAllFriendRequestPack, clientInfo);
        return ResponseVO.successResponse();
    }
}
