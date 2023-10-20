package com.jiangjing.im.service.utils;

import com.alibaba.fastjson2.JSONObject;
import com.jiangjing.im.common.enums.ClientType;
import com.jiangjing.im.common.enums.command.Command;
import com.jiangjing.im.common.enums.command.GroupEventCommand;
import com.jiangjing.im.common.model.ClientInfo;
import com.jiangjing.im.service.group.model.req.GroupMemberDto;
import com.jiangjing.im.service.group.service.ImGroupMemberService;
import com.jiangjing.pack.group.AddGroupMemberPack;
import com.jiangjing.pack.group.RemoveGroupMemberPack;
import com.jiangjing.pack.group.UpdateGroupMemberPack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 群组的消息发送，需要通知群成员的信息
 *
 * @author
 */
@Component
public class GroupMessageProducer {

    @Autowired
    ImGroupMemberService imGroupMemberService;

    @Autowired
    MessageProducer messageProducer;

    /**
     * 群组消息的通知
     *
     * @param userId
     * @param appId      appid
     * @param groupId    群组id
     * @param command    命令类型
     * @param message    需要发送的消息
     * @param clientInfo 发送者的客户端消息
     */
    public void sendMessage(String userId, Integer appId, @NotNull String groupId, Command command, Object message, ClientInfo clientInfo) {

        // 将需要发送方的对象转成 JsonObject
        JSONObject messageObj = JSONObject.from(message);

        /**
         * 根据不同的命令类型，判断具体需要发送方的用户
         */
        // 1、新增群成员，只需要通知 加入者和群管理员即可
        if (command.equals(GroupEventCommand.ADDED_MEMBER)) {
            // 获取群管理员
            List<GroupMemberDto> groupManagers = imGroupMemberService.getGroupManagers(groupId, appId);
            List<String> members = messageObj.toJavaObject(AddGroupMemberPack.class).getMembers();
            // 遍历发送消息
            for (String memberId : members) {
                // 如果当前用户操作发起端 不是 webapi ，那么只需要通知其他的登录端进行书话剧同步即可
                if (clientInfo.getClientType() != null && clientInfo.getClientType() !=
                        ClientType.WEBAPI.getCode() && memberId.equals(userId)) {
                    messageProducer.sendToUserExceptClient(memberId, command,
                            message, clientInfo);
                } else {
                    // 其他群成员需要所有端都需要通知
                    messageProducer.sendToUserByAll(memberId, appId, command, message);
                }
            }

            for (GroupMemberDto groupManager : groupManagers) {
                // 如果当前用户操作发起端 不是 webapi ，那么只需要通知其他的登录端进行书话剧同步即可
                if (clientInfo.getClientType() != null && clientInfo.getClientType() !=
                        ClientType.WEBAPI.getCode() && groupManager.getMemberId().equals(userId)) {
                    messageProducer.sendToUserExceptClient(groupManager.getMemberId(), command,
                            message, clientInfo);
                } else {
                    // 其他群成员需要所有端都需要通知
                    messageProducer.sendToUserByAll(groupManager.getMemberId(), appId, command, message);
                }
            }
            // 2、退群通知全体群成员成员，因为是剔除成功之后才做的回到，所以需要将当前退出的人员id手动添加
        } else if (command.equals(GroupEventCommand.DELETED_MEMBER)) {
            // 获取当前的群成员
            List<String> memberIds = imGroupMemberService.getGroupMemberIds(groupId, appId);
            String memberId = messageObj.toJavaObject(RemoveGroupMemberPack.class).getMember();
            memberIds.add(memberId);
            for (String member : memberIds) {
                if (clientInfo.getClientType() != null && clientInfo.getClientType() !=
                        ClientType.WEBAPI.getCode() && memberId.equals(userId)) {
                    messageProducer.sendToUserExceptClient(member, command,
                            message, clientInfo);
                } else {
                    messageProducer.sendToUserByAll(member, appId, command, message);
                }
            }

            // 更新群成员信息——昵称，只需要通知自身其他的客户端和群管理员
        } else if (command.equals(GroupEventCommand.UPDATED_MEMBER)) {
            List<GroupMemberDto> groupManagers = imGroupMemberService.getGroupManagers(groupId, appId);
            String memberId = messageObj.toJavaObject(UpdateGroupMemberPack.class).getMemberId();
            // 判断当前被修改的用户是否就是管理员
            boolean match = groupManagers.stream().noneMatch(groupMemberDto -> memberId.equals(groupMemberDto.getMemberId()));
            if (match) {
                GroupMemberDto groupMemberDto = new GroupMemberDto();
                groupMemberDto.setMemberId(memberId);
            }
            for (GroupMemberDto member : groupManagers) {
                if (clientInfo.getClientType() != null && clientInfo.getClientType() !=
                        ClientType.WEBAPI.getCode() && memberId.equals(userId)) {
                    messageProducer.sendToUserExceptClient(member.getMemberId(), command,
                            message, clientInfo);
                } else {
                    messageProducer.sendToUserByAll(member.getMemberId(), appId, command, message);
                }
            }
        } else {
            List<String> memberIds = imGroupMemberService.getGroupMemberIds(groupId, appId);
            for (String memberId : memberIds) {
                // 如果当前用户操作发起端 不是 webapi ，那么只需要通知其他的登录端进行书话剧同步即可
                if (clientInfo.getClientType() != null && clientInfo.getClientType() !=
                        ClientType.WEBAPI.getCode() && memberId.equals(userId)) {
                    messageProducer.sendToUserExceptClient(memberId, command,
                            message, clientInfo);
                } else {
                    // 其他群成员需要所有端都需要通知
                    messageProducer.sendToUserByAll(memberId, appId, command, message);
                }
            }
        }
    }
}
