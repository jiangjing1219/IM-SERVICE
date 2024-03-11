package com.jiangjing.im.service.group.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.config.AppConfig;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.GroupErrorCode;
import com.jiangjing.im.common.enums.GroupMemberRoleEnum;
import com.jiangjing.im.common.enums.GroupStatusEnum;
import com.jiangjing.im.common.enums.GroupTypeEnum;
import com.jiangjing.im.common.enums.command.GroupEventCommand;
import com.jiangjing.im.common.exception.ApplicationException;
import com.jiangjing.im.common.model.ClientInfo;
import com.jiangjing.im.service.group.dao.ImGroupEntity;
import com.jiangjing.im.service.group.dao.ImGroupMemberEntity;
import com.jiangjing.im.service.group.dao.mapper.ImGroupMemberMapper;
import com.jiangjing.im.service.group.model.callback.AddMemberAfterCallback;
import com.jiangjing.im.service.group.model.req.*;
import com.jiangjing.im.service.group.model.resp.AddMemberResp;
import com.jiangjing.im.service.group.model.resp.GetRoleInGroupResp;
import com.jiangjing.im.service.group.service.ImGroupMemberService;
import com.jiangjing.im.service.group.service.ImGroupService;
import com.jiangjing.im.service.user.service.ImUserService;
import com.jiangjing.im.service.utils.CallbackService;
import com.jiangjing.im.service.utils.GroupMessageProducer;
import com.jiangjing.pack.group.AddGroupMemberPack;
import com.jiangjing.pack.group.GroupMemberSpeakPack;
import com.jiangjing.pack.group.RemoveGroupMemberPack;
import com.jiangjing.pack.group.UpdateGroupMemberPack;
import com.jiangjing.im.service.user.dao.ImUserDataEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author jingjing
 * @date 2023/5/25 0:19
 */
@Service
@Transactional
public class ImGroupMemberServiceImpl implements ImGroupMemberService {

    private final static Logger logger = LoggerFactory.getLogger(ImGroupMemberServiceImpl.class);


    @Autowired
    ImGroupMemberMapper imGroupMemberMapper;

    @Autowired
    ImGroupService imGroupService;

    @Autowired
    AppConfig appConfig;

    @Autowired
    CallbackService callbackService;

    @Autowired
    GroupMessageProducer groupMessageProducer;

    @Autowired
    ImUserService imUserService;


    /**
     * 新增群成员信息
     *
     * @param groupId        群主键id
     * @param appId          appId
     * @param groupMemberDto 群成员信息
     */
    @Override
    public ResponseVO addGroupMember(String groupId, Integer appId, GroupMemberDto groupMemberDto) {

        // 当前按新增的群成员信息，角色的群主
        if (groupMemberDto.getRole() != null && GroupMemberRoleEnum.OWNER.getCode() == groupMemberDto.getRole()) {
            // 判断当前群是否已经存在群主
            QueryWrapper<ImGroupMemberEntity> queryOwner = new QueryWrapper<>();
            queryOwner.eq("group_id", groupId);
            queryOwner.eq("app_id", appId);
            queryOwner.eq("role", GroupMemberRoleEnum.OWNER.getCode());
            Integer ownerNum = imGroupMemberMapper.selectCount(queryOwner);
            if (ownerNum > 0) {
                // 已经存在群主
                return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_HAVE_OWNER);
            }
        }

        // 校验是否已经存在该群成员
        QueryWrapper<ImGroupMemberEntity> query = new QueryWrapper<>();
        query.eq("group_id", groupId);
        query.eq("app_id", appId);
        query.eq("member_id", groupMemberDto.getMemberId());
        ImGroupMemberEntity memberDto = imGroupMemberMapper.selectOne(query);
        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(groupMemberDto.getMemberId(), appId);
        if (memberDto == null) {
            // 执行新增操作,java 规范不将入参作为变量
            memberDto = new ImGroupMemberEntity();
            BeanUtils.copyProperties(groupMemberDto, memberDto);
            memberDto.setAlias(toInfo.isOk() ? toInfo.getData().getNickName() : "");
            memberDto.setGroupId(groupId);
            memberDto.setAppId(appId);
            memberDto.setJoinTime(System.currentTimeMillis());
            int insert = imGroupMemberMapper.insert(memberDto);
            if (insert == 1) {
                return ResponseVO.successResponse();
            }
            return ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
        } else if (GroupMemberRoleEnum.LEAVE.getCode() == memberDto.getRole()) {
            // 判断当前群成员的的状态，如果是已经离群状态，那么需要更新的状态
            memberDto = new ImGroupMemberEntity();
            BeanUtils.copyProperties(groupMemberDto, memberDto);
            memberDto.setJoinTime(System.currentTimeMillis());
            memberDto.setAlias(toInfo.isOk() ? toInfo.getData().getNickName() : "");
            int update = imGroupMemberMapper.update(memberDto, query);
            if (update == 1) {
                return ResponseVO.successResponse();
            }
            return ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
        }
        return ResponseVO.errorResponse(GroupErrorCode.USER_IS_JOINED_GROUP);
    }

    /**
     * 获取群成员信息
     *
     * @param groupId
     * @param appId
     * @return
     */
    @Override
    public ResponseVO<List<GroupMemberDto>> getGroupMember(String groupId, Integer appId) {
        List<GroupMemberDto> imGroupMemberEntities = imGroupMemberMapper.getGroupMember(appId,groupId);
        return ResponseVO.successResponse(imGroupMemberEntities);
    }

    @Override
    public List<String> getGroupMemberIds(String groupId, Integer appId) {
        return imGroupMemberMapper.getGroupMemberIds(appId, groupId);
    }

    @Override
    public List<GroupMemberDto> getGroupManagers(String groupId, Integer appId) {
        return imGroupMemberMapper.getGroupManagers(groupId, appId);
    }

    /**
     * 获取指定群成员的信息
     *
     * @param groupId
     * @param memberId
     * @param appId
     * @return
     */
    @Override
    public ResponseVO<GetRoleInGroupResp> getRoleInGroupOne(String groupId, String memberId, Integer appId) {
        QueryWrapper<ImGroupMemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        queryWrapper.eq("member_id", memberId);
        queryWrapper.eq("app_id", appId);
        ImGroupMemberEntity imGroupMemberEntity = imGroupMemberMapper.selectOne(queryWrapper);
        // 如果当前群成员不存在的获取是已经退群状态
        if (imGroupMemberEntity == null || imGroupMemberEntity.getRole() == GroupMemberRoleEnum.LEAVE.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }
        GetRoleInGroupResp resp = new GetRoleInGroupResp();
        resp.setSpeakDate(imGroupMemberEntity.getSpeakDate());
        resp.setGroupMemberId(imGroupMemberEntity.getGroupMemberId());
        resp.setMemberId(imGroupMemberEntity.getMemberId());
        resp.setRole(imGroupMemberEntity.getRole());
        return ResponseVO.successResponse(resp);
    }

    /**
     * 获取当前用户加入的所有的群id集合
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO<Collection<String>> getMemberJoinedGroup(GetJoinedGroupReq req) {
        // 分页限制
        if (req.getLimit() != null) {
            Page<ImGroupMemberEntity> objectPage = new Page<>(req.getOffset(), req.getLimit());
            QueryWrapper<ImGroupMemberEntity> query = new QueryWrapper<>();
            query.eq("app_id", req.getAppId());
            query.eq("member_id", req.getMemberId());
            IPage<ImGroupMemberEntity> imGroupMemberEntityPage = imGroupMemberMapper.selectPage(objectPage, query);
            Set<String> groupId = new HashSet<>();
            List<ImGroupMemberEntity> records = imGroupMemberEntityPage.getRecords();
            records.forEach(e -> {
                groupId.add(e.getGroupId());
            });

            return ResponseVO.successResponse(groupId);
        } else {
            return ResponseVO.successResponse(imGroupMemberMapper.getJoinedGroupId(req.getAppId(), req.getMemberId()));
        }
    }

    /**
     * 导入群成员信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO importGroupMember(ImportGroupMemberReq req) {
        List<AddMemberResp> resp = new ArrayList<>();
        // 获取群信息
        ResponseVO<ImGroupEntity> groupResp = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }
        if (groupResp.getData().getStatus().equals(GroupStatusEnum.DESTROY.getCode())) {
            return ResponseVO.errorResponse(GroupErrorCode.THIS_GROUP_IS_DESTROY.getCode(), GroupErrorCode.THIS_GROUP_IS_DESTROY.name());
        }
        req.getMembers().forEach(member -> {
            ResponseVO responseVO = this.addGroupMember(req.getGroupId(), req.getAppId(), member);
            AddMemberResp addMemberResp = new AddMemberResp();
            addMemberResp.setMemberId(member.getMemberId());
            if (responseVO.isOk()) {
                addMemberResp.setResult(0);
            } else if (responseVO.getCode() == GroupErrorCode.USER_IS_JOINED_GROUP.getCode()) {
                addMemberResp.setResult(2);
            } else {
                addMemberResp.setResult(1);
            }
            resp.add(addMemberResp);
        });
        return ResponseVO.successResponse(resp);
    }

    /**
     * 批量拉用户进群
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO addMember(AddGroupMemberReq req) {

        List<AddMemberResp> resp = new ArrayList<>();

        boolean isAdmin = false;
        ResponseVO<ImGroupEntity> groupResp = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }

        // 需要添加的用户
        List<GroupMemberDto> memberDtos = req.getMembers();

        if (appConfig.isAddGroupMemberBeforeCallback()) {

            ResponseVO responseVO = callbackService.beforeCallback(req.getAppId(), Constants.CallbackCommand.GROUP_MEMBER_ADD_BEFORE
                    , JSON.toJSONString(req));
            if (!responseVO.isOk()) {
                return responseVO;
            }

            try {
                // 使用回调之后的需要需要添加的用户信息，可以在回调中自定义拦截
                memberDtos = JSON.parseArray(JSON.toJSONString(responseVO.getData()), GroupMemberDto.class);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("GroupMemberAddBefore 回调失败：{}", req.getAppId());
            }
        }
        // 群信息
        ImGroupEntity group = groupResp.getData();
        /**
         * 私有群（private）	类似普通微信群，创建后仅支持已在群内的好友邀请加群，且无需被邀请方同意或群主审批
         * 公开群（Public）	类似 QQ 群，创建后群主可以指定群管理员，需要群主或管理员审批通过才能入群
         * 群类型 1私有群（类似微信） 2公开群(类似qq）
         *
         */
        if (!isAdmin && GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_APPMANAGER_ROLE);
        }
        List<String> successId = new ArrayList<>();
        memberDtos.forEach(memberDto -> {
            ResponseVO responseVO = this.addGroupMember(req.getGroupId(), req.getAppId(), memberDto);
            AddMemberResp addMemberResp = new AddMemberResp();
            addMemberResp.setMemberId(memberDto.getMemberId());
            if (responseVO.isOk()) {
                successId.add(memberDto.getMemberId());
                addMemberResp.setResult(0);
            } else if (responseVO.getCode() == GroupErrorCode.USER_IS_JOINED_GROUP.getCode()) {
                addMemberResp.setResult(2);
                addMemberResp.setResultMessage(responseVO.getMessage());
            } else {
                addMemberResp.setResult(1);
                addMemberResp.setResultMessage(responseVO.getMessage());
            }
            resp.add(addMemberResp);
        });

        // 添加完群成员之后，通知各群成员
        AddGroupMemberPack addGroupMemberPack = new AddGroupMemberPack();
        addGroupMemberPack.setGroupId(req.getGroupId());
        addGroupMemberPack.setMembers(successId);
        groupMessageProducer.sendMessage(req.getOperate(), req.getAppId(), req.getGroupId(), GroupEventCommand.ADDED_MEMBER, addGroupMemberPack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        // 添加完群成员之后，回调业务接口
        if (appConfig.isAddGroupMemberAfterCallback()) {
            AddMemberAfterCallback dto = new AddMemberAfterCallback();
            dto.setGroupId(req.getGroupId());
            dto.setGroupType(group.getGroupType());
            dto.setMemberId(resp);
            dto.setOperater(req.getOperate());
            callbackService.callback(req.getAppId()
                    , Constants.CallbackCommand.GROUP_MEMBER_ADD_AFTER,
                    JSON.toJSONString(dto));
        }
        return ResponseVO.successResponse(resp);
    }

    /**
     * 删除群成员
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO removeMember(RemoveGroupMemberReq req) {
        boolean isAdmin = false;
        // 获取群信息
        ResponseVO<ImGroupEntity> groupResp = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }
        ImGroupEntity group = groupResp.getData();

        if (!isAdmin) {
            if (GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
                //获取操作人的权限 是管理员or群主or群成员
                ResponseVO<GetRoleInGroupResp> role = getRoleInGroupOne(req.getGroupId(), req.getOperate(), req.getAppId());
                if (!role.isOk()) {
                    return role;
                }

                GetRoleInGroupResp data = role.getData();
                Integer roleInfo = data.getRole();
                // 是否是群主
                boolean isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
                // 是否是管理员
                boolean isManager = roleInfo == GroupMemberRoleEnum.MAMAGER.getCode();

                // 既不是群主也不是管理员，无权限操作
                if (!isOwner && !isManager) {
                    throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }

                //私有群必须是群主才能踢人
                if (!isOwner && GroupTypeEnum.PRIVATE.getCode() == group.getGroupType()) {
                    throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }

                //公开群管理员和群主可踢人，但管理员只能踢普通群成员
                if (GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
                    // 获取被踢成员的信息
                    ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
                    if (!roleInGroupOne.isOk()) {
                        return roleInGroupOne;
                    }
                    GetRoleInGroupResp memberRole = roleInGroupOne.getData();
                    // 群主不能被踢
                    if (memberRole.getRole() == GroupMemberRoleEnum.OWNER.getCode()) {
                        throw new ApplicationException(GroupErrorCode.GROUP_OWNER_IS_NOT_REMOVE);
                    }
                    //是管理员并且被踢人不是群成员，无法操作
                    if (isManager && memberRole.getRole() != GroupMemberRoleEnum.ORDINARY.getCode()) {
                        throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                    }
                }

            }
        }
        ResponseVO responseVO = this.removeGroupMember(req.getGroupId(), req.getAppId(), req.getMemberId());
        if (responseVO.isOk()) {
            // 同步登录端，通知其他群群成员
            RemoveGroupMemberPack removeGroupMemberPack = new RemoveGroupMemberPack();
            removeGroupMemberPack.setGroupId(req.getGroupId());
            removeGroupMemberPack.setMember(req.getMemberId());
            groupMessageProducer.sendMessage(req.getOperate(), req.getAppId(), req.getGroupId(), GroupEventCommand.DELETED_MEMBER, removeGroupMemberPack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

            // 回调业务操作
            if (appConfig.isDeleteGroupMemberAfterCallback()) {
                callbackService.callback(req.getAppId(),
                        Constants.CallbackCommand.GROUP_MEMBER_DELETE_AFTER,
                        JSON.toJSONString(req));
            }
        }
        return responseVO;
    }

    /**
     * 更改群成员信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO updateGroupMember(UpdateGroupMemberReq req) {
        // 是否是后台管理员
        boolean isAdmin = false;
        // 查询群信息
        ResponseVO<ImGroupEntity> group = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (!group.isOk()) {
            return group;
        }

        ImGroupEntity groupData = group.getData();

        //是否是自己修改自己的资料
        boolean isMeOperate = req.getOperate().equals(req.getMemberId());
        if (!isAdmin) {
            //昵称只能自己修改 权限只能群主或管理员修改
            if (StringUtils.isBlank(req.getAlias()) && !isMeOperate) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_ONESELF);
            }

            //私有群不能设置管理员，校验更新的角色信息，也不能直接更新他人的为管理员(不能通过信息变更的方式设置群主信息，只能走群主变更接口)
            if (groupData.getGroupType() == GroupTypeEnum.PRIVATE.getCode() &&
                    req.getRole() != null && (req.getRole() == GroupMemberRoleEnum.MAMAGER.getCode() ||
                    req.getRole() == GroupMemberRoleEnum.OWNER.getCode())) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            // 获取当前操作人员的信息
            ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getOperate(), req.getAppId());
            if (!roleInGroupOne.isOk()) {
                return roleInGroupOne;
            }

            GetRoleInGroupResp data = roleInGroupOne.getData();
            Integer roleInfo = data.getRole();
            // 是否是群主
            boolean isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
            //是否是管理员
            boolean isManager = roleInfo == GroupMemberRoleEnum.MAMAGER.getCode();
            //不是管理员不能修改权限
            if (req.getRole() != null && !isOwner && !isManager) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
            //管理员只有群主能够设置
            if (req.getRole() != null && req.getRole() == GroupMemberRoleEnum.MAMAGER.getCode() && !isOwner) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }
        ImGroupMemberEntity update = new ImGroupMemberEntity();
        if (StringUtils.isNotBlank(req.getAlias())) {
            update.setAlias(req.getAlias());
        }
        if (req.getRole() != null) {
            update.setRole(req.getRole());
        }
        update.setAlias(req.getAlias());
        UpdateWrapper<ImGroupMemberEntity> objectUpdateWrapper = new UpdateWrapper<>();
        objectUpdateWrapper.eq("app_id", req.getAppId());
        objectUpdateWrapper.eq("member_id", req.getMemberId());
        objectUpdateWrapper.eq("group_id", req.getGroupId());
        imGroupMemberMapper.update(update, objectUpdateWrapper);

        // 同步登录端，通知其他群成员
        UpdateGroupMemberPack pack = new UpdateGroupMemberPack();
        BeanUtils.copyProperties(req, pack);
        groupMessageProducer.sendMessage(req.getOperate(), req.getAppId(), req.getGroupId(), GroupEventCommand.UPDATED_MEMBER, pack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        return ResponseVO.successResponse();
    }

    /**
     * 设置禁言
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO speak(SpeaMemberReq req) {
        // 查询群信息
        ResponseVO<ImGroupEntity> groupResp = imGroupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }

        boolean isAdmin = false;
        boolean isOwner = false;
        boolean isManager = false;
        GetRoleInGroupResp memberRole = null;

        // 不是后台管理员，需要校验权限
        if (!isAdmin) {
            //获取操作人的权限 是管理员or群主or群成员
            ResponseVO<GetRoleInGroupResp> role = getRoleInGroupOne(req.getGroupId(), req.getOperate(), req.getAppId());
            if (!role.isOk()) {
                return role;
            }

            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();
            isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
            isManager = roleInfo == GroupMemberRoleEnum.MAMAGER.getCode();

            // 只有群主或者管理员才能禁言他人
            if (!isOwner && !isManager) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            //获取被禁言对象的权限
            ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
            if (!roleInGroupOne.isOk()) {
                return roleInGroupOne;
            }
            memberRole = roleInGroupOne.getData();
            //被操作人是群主只能app管理员操作
            if (memberRole.getRole() == GroupMemberRoleEnum.OWNER.getCode()) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_APPMANAGER_ROLE);
            }

            //是管理员并且被操作人不是群成员，无法操作
            if (isManager && memberRole.getRole() != GroupMemberRoleEnum.ORDINARY.getCode()) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        // 设置被禁言对象的id
        imGroupMemberEntity.setGroupMemberId(memberRole.getGroupMemberId());
        if (req.getSpeakDate() > 0) {
            imGroupMemberEntity.setSpeakDate(System.currentTimeMillis() + req.getSpeakDate());
        } else {
            imGroupMemberEntity.setSpeakDate(req.getSpeakDate());
        }
        imGroupMemberMapper.updateById(imGroupMemberEntity);

        // 禁言标识同步
        GroupMemberSpeakPack pack = new GroupMemberSpeakPack();
        BeanUtils.copyProperties(req, pack);
        groupMessageProducer.sendMessage(req.getOperate(), req.getAppId(), req.getGroupId(), GroupEventCommand.SPEAK_GROUP_MEMBER, pack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO transferGroupMember(String ownerId, String groupId, Integer appId) {

        // 变更群主的角色为普通成员
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.ORDINARY.getCode());
        QueryWrapper<ImGroupMemberEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("group_id", groupId);
        wrapper.eq("app_id", appId);
        wrapper.eq("role", GroupMemberRoleEnum.OWNER.getCode());
        imGroupMemberMapper.update(imGroupMemberEntity, wrapper);

        // 变更被转让者的角色
        ImGroupMemberEntity newOwner = new ImGroupMemberEntity();
        newOwner.setRole(GroupMemberRoleEnum.OWNER.getCode());
        QueryWrapper<ImGroupMemberEntity> newUpdate = new QueryWrapper<>();
        newUpdate.eq("group_id", groupId);
        newUpdate.eq("app_id", appId);
        newUpdate.eq("member_id", ownerId);
        imGroupMemberMapper.update(newOwner, newUpdate);

        return ResponseVO.successResponse();
    }

    /**
     * 获取执行用户加入的群聊，排除指定角色
     *
     * @param appId
     * @param userId
     * @param excludeRole
     * @return
     */
    @Override
    public List<String> getMemberJoinedGroupByCondition(Integer appId, String userId, int excludeRole) {
        return imGroupMemberMapper.getMemberJoinedGroupByCondition(appId, userId, excludeRole);
    }

    private ResponseVO removeGroupMember(String groupId, Integer appId, String memberId) {
        ResponseVO<GetRoleInGroupResp> roleInGroupOne = getRoleInGroupOne(groupId, memberId, appId);
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }
        GetRoleInGroupResp data = roleInGroupOne.getData();
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.LEAVE.getCode());
        imGroupMemberEntity.setGroupMemberId(data.getGroupMemberId());
        imGroupMemberMapper.updateById(imGroupMemberEntity);
        return ResponseVO.successResponse();
    }
}
