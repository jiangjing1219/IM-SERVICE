package com.jiangjing.im.service.message.service;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.config.AppConfig;
import com.jiangjing.im.common.enums.*;
import com.jiangjing.im.service.friendship.dao.ImFriendShipEntity;
import com.jiangjing.im.service.friendship.model.req.GetRelationReq;
import com.jiangjing.im.service.friendship.service.ImFriendService;
import com.jiangjing.im.service.group.dao.ImGroupEntity;
import com.jiangjing.im.service.group.model.resp.GetRoleInGroupResp;
import com.jiangjing.im.service.group.service.ImGroupMemberService;
import com.jiangjing.im.service.group.service.ImGroupService;
import com.jiangjing.im.service.user.dao.ImUserDataEntity;
import com.jiangjing.im.service.user.service.ImUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 发送消息的前置校验
 *
 * @author
 */
@Service
public class CheckSendMessageService {

    @Autowired
    ImUserService imUserService;

    @Autowired
    ImFriendService imFriendService;

    @Autowired
    ImGroupService imGroupService;

    @Autowired
    ImGroupMemberService imGroupMemberService;

    @Autowired
    AppConfig appConfig;

    /**
     * 校验当前用户是否被禁言/禁用
     *
     * @param fromId
     * @param appId
     * @return
     */
    public ResponseVO checkSenderFervidAndMute(String fromId, Integer appId) {

        ResponseVO responseVO = imUserService.getSingleUserInfo(fromId, appId);
        if (!responseVO.isOk()) {
            return responseVO;
        }
        ImUserDataEntity imUserDataEntity = (ImUserDataEntity) responseVO.getData();
        // 判断禁用标识
        if (imUserDataEntity.getForbiddenFlag() == UserForbiddenFlagEnum.FORBIBBEN.getCode()) {
            ResponseVO.errorResponse(MessageErrorCode.FROMER_IS_FORBIBBEN);
        }
        // 判断禁言标识
        if (imUserDataEntity.getSilentFlag() == UserSilentFlagEnum.MUTE.getCode()) {
            return ResponseVO.errorResponse(MessageErrorCode.FROMER_IS_MUTE);
        }
        return ResponseVO.successResponse();
    }

    /**
     * 判断发送方和接收方的好友关系是否正常
     *
     * @param fromId
     * @param toId
     * @param appId
     * @return
     */
    public ResponseVO checkFriendShip(String fromId, String toId, Integer appId) {
        // 发送消息时，是否判断好友关系的开关，正式上线时需要一张 app 的配置表进行维护
        if (appConfig.isSendMessageCheckFriend()) {

            // 1、获取发送方的好友关系
            GetRelationReq fromReq = new GetRelationReq();
            fromReq.setFromId(fromId);
            fromReq.setToId(toId);
            fromReq.setAppId(appId);
            ResponseVO<ImFriendShipEntity> fromRelation = imFriendService.getRelation(fromReq);
            if (!fromRelation.isOk()) {
                return fromRelation;
            }

            // 2、获取接收方的好友关系
            GetRelationReq toReq = new GetRelationReq();
            fromReq.setFromId(toId);
            fromReq.setToId(fromId);
            fromReq.setAppId(appId);
            ResponseVO<ImFriendShipEntity> toRelation = imFriendService.getRelation(toReq);
            if (!toRelation.isOk()) {
                return toRelation;
            }

            // 3、判断发送方的好友状态，如果不是正常状态，那么说明已经发送方已经吧接收方删除
            if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() != fromRelation.getData().getStatus()) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
            }

            // 4、判断接收方的好友关系，
            if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() != toRelation.getData().getStatus()) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED_YOU);
            }

            if (appConfig.isSendMessageCheckBlack()) {
                // 5、判断发送放的拉黑状态
                if (FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode() != fromRelation.getData().getBlack()) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_BLACK);
                }
                // 6、判断接收方的拉黑状态
                if (FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode() != toRelation.getData().getBlack()) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.TARGET_IS_BLACK_YOU);
                }
            }
        }
        return ResponseVO.successResponse();
    }


    /**
     * 群聊消息前置校验
     *
     * @param fromId
     * @param groupId
     * @param appId
     * @return
     */
    public ResponseVO checkGroupMessage(String fromId, String groupId, Integer appId) {
        // 1、校验发送方本身的
        ResponseVO responseVO = this.checkSenderFervidAndMute(fromId, appId);
        if (!responseVO.isOk()) {
            return responseVO;
        }

        // 2、校验群信息是否正常
        ResponseVO<ImGroupEntity> groupEntityResponseVO = imGroupService.getGroup(groupId, appId);
        if (!groupEntityResponseVO.isOk()) {
            return groupEntityResponseVO;
        }

        // 3、校验发送用户是否是群成员
        ResponseVO<GetRoleInGroupResp> roleInGroupOne = imGroupMemberService.getRoleInGroupOne(groupId, fromId, appId);
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }
        GetRoleInGroupResp roleInGroup = roleInGroupOne.getData();
        ImGroupEntity imGroupEntity = groupEntityResponseVO.getData();
        // 4、判断群的禁言状态（禁言时只有群管理和群主可以发言）
        if (imGroupEntity.getMute() == GroupMuteTypeEnum.MUTE.getCode() && roleInGroup.getRole() == GroupMemberRoleEnum.ORDINARY.getCode()) {
            // 群处于禁言状态，并且发消息的用户只是普通用户
            return ResponseVO.errorResponse(GroupErrorCode.THIS_GROUP_IS_MUTE);
        }
        // 5、判断单个用户自身的禁言状态，可能时管理员将普通成员禁言
        if(roleInGroup.getSpeakDate() != null && roleInGroup.getSpeakDate() > System.currentTimeMillis()){
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_MEMBER_IS_SPEAK);
        }
        return ResponseVO.successResponse();
    }
}
