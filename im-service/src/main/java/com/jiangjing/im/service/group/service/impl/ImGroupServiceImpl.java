package com.jiangjing.im.service.group.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
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
import com.jiangjing.im.common.model.SyncReq;
import com.jiangjing.im.common.model.SyncResp;
import com.jiangjing.im.service.group.dao.ImGroupEntity;
import com.jiangjing.im.service.group.dao.mapper.ImGroupDataMapper;
import com.jiangjing.im.service.group.model.callback.DestroyGroupCallbackDto;
import com.jiangjing.im.service.group.model.req.*;
import com.jiangjing.im.service.group.model.resp.GetGroupResp;
import com.jiangjing.im.service.group.model.resp.GetJoinedGroupResp;
import com.jiangjing.im.service.group.model.resp.GetRoleInGroupResp;
import com.jiangjing.im.service.group.service.ImGroupMemberService;
import com.jiangjing.im.service.group.service.ImGroupService;
import com.jiangjing.im.service.sequence.RedisSeq;
import com.jiangjing.im.service.utils.CallbackService;
import com.jiangjing.im.service.utils.GroupMessageProducer;
import com.jiangjing.pack.group.CreateGroupPack;
import com.jiangjing.pack.group.DestroyGroupPack;
import com.jiangjing.pack.group.UpdateGroupInfoPack;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 群组的增来奶更新：由于群组涉及的群成员比较对，在变更每个群成员的 seq 就变成了重型操作，优化：不在维护 redis 缓存，在群信息发生变更时还是变更seq，但是服务端判断是否有增量更新，直接查询数据数据库中加入的群组最大的seq，和本地的seq做对比来判断
 *
 * @author jingjing
 * @date 2023/5/24 23:52
 */
@Service
@Transactional
public class ImGroupServiceImpl implements ImGroupService {

    @Autowired
    ImGroupDataMapper imGroupDataMapper;

    @Autowired
    ImGroupMemberService imGroupMemberService;

    @Autowired
    AppConfig appConfig;

    @Autowired
    CallbackService callbackService;

    @Autowired
    GroupMessageProducer groupMessageProducer;

    @Autowired
    RedisSeq redisSeq;

    /**
     * 新增群信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO importGroup(ImportGroupReq req) {

        //1.判断群id是否存在，如果不存在需要自定义生成
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
        if (StringUtils.isBlank(req.getGroupId())) {
            // uuid 作为群主键
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            // 需要判断新增的自定义id是否已经存在
            query.eq("group_id", req.getGroupId());
            query.eq("app_id", req.getAppId());
            Integer integer = imGroupDataMapper.selectCount(query);
            if (integer > 0) {
                throw new ApplicationException(GroupErrorCode.GROUP_IS_EXIST);
            }
        }

        ImGroupEntity imGroupEntity = new ImGroupEntity();

        // 2、如果当前群是公开群，那么 OwnerId 就不能为空，需要指定群主是谁
        if (req.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && StringUtils.isBlank(req.getOwnerId())) {
            throw new ApplicationException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }
        // 设置创建时间
        if (req.getCreateTime() == null) {
            imGroupEntity.setCreateTime(System.currentTimeMillis());
        }
        // 设置群状态为 正常
        imGroupEntity.setStatus(GroupStatusEnum.NORMAL.getCode());
        BeanUtils.copyProperties(req, imGroupEntity);
        // 单表新增
        int insert = imGroupDataMapper.insert(imGroupEntity);

        if (insert != 1) {
            throw new ApplicationException(GroupErrorCode.IMPORT_GROUP_ERROR);
        }
        return ResponseVO.successResponse();
    }

    /**
     * 创建群
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO createGroup(CreateGroupReq req) {

        // 1、判断是否是管理员，如果不是管理员，那么当前操作人员就是群主
        boolean isAdmin = false;
        if (!isAdmin) {
            req.setOwnerId(req.getOperate());
        }

        // 2、判断群ID是否存在
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
        if (StringUtils.isBlank(req.getGroupId())) {
            // uuid 作为群主键
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            // 需要判断新增的自定义id是否已经存在
            query.eq("group_id", req.getGroupId());
            query.eq("app_id", req.getAppId());
            Integer integer = imGroupDataMapper.selectCount(query);
            if (integer > 0) {
                throw new ApplicationException(GroupErrorCode.GROUP_IS_EXIST);
            }
        }

        if (req.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && StringUtils.isBlank(req.getOwnerId())) {
            throw new ApplicationException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }

        // 3、新增群信息
        ImGroupEntity imGroupEntity = new ImGroupEntity();
        long redisSeqSeq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.GROUP_SEQ);
        imGroupEntity.setCreateTime(System.currentTimeMillis());
        imGroupEntity.setStatus(GroupStatusEnum.NORMAL.getCode());
        imGroupEntity.setSequence(redisSeqSeq);
        BeanUtils.copyProperties(req, imGroupEntity);
        imGroupDataMapper.insert(imGroupEntity);

        // 群主也是群成员
        GroupMemberDto groupMemberDto = new GroupMemberDto();
        groupMemberDto.setMemberId(req.getOwnerId());
        groupMemberDto.setRole(GroupMemberRoleEnum.OWNER.getCode());
        groupMemberDto.setJoinTime(System.currentTimeMillis());
        imGroupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), groupMemberDto);

        //插入群成员
        for (GroupMemberDto ggroupMemberDto : req.getMember()) {
            imGroupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), ggroupMemberDto);
        }

        // 创建成功之后的消息通知
        CreateGroupPack createGroupPack = new CreateGroupPack();
        BeanUtils.copyProperties(imGroupEntity, createGroupPack);
        createGroupPack.setSequence(redisSeqSeq);
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setAppId(req.getAppId());
        clientInfo.setImei(req.getImei());
        clientInfo.setClientType(req.getClientType());
        groupMessageProducer.sendMessage(req.getOperate(), req.getAppId(), imGroupEntity.getGroupId(), GroupEventCommand.CREATED_GROUP, createGroupPack, clientInfo);

        // 新增群之后的回调
        if (appConfig.isCreateGroupAfterCallback()) {
            callbackService.callback(req.getAppId(), Constants.CallbackCommand.CREATE_GROUP_AFTER,
                    JSON.toJSONString(imGroupEntity));
        }
        return ResponseVO.successResponse();
    }

    /**
     * 获取指定的群信息,包含群成员信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO getGroup(GetGroupReq req) {
        ResponseVO responseVO = this.getGroup(req.getGroupId(), req.getAppId());
        if (!responseVO.isOk()) {
            return responseVO;
        }
        // 查询群成员信息
        GetGroupResp getGroupResp = new GetGroupResp();
        BeanUtils.copyProperties(responseVO.getData(), getGroupResp);
        ResponseVO<List<GroupMemberDto>> listResponseVO = imGroupMemberService.getGroupMember(req.getGroupId(), req.getAppId());
        if (listResponseVO.isOk()) {
            getGroupResp.setMemberList(listResponseVO.getData());
        }
        return ResponseVO.successResponse(getGroupResp);
    }

    @Override
    public ResponseVO getGroup(String groupId, Integer appId) {
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
        query.eq("app_id", appId);
        query.eq("group_id", groupId);
        ImGroupEntity imGroupEntity = imGroupDataMapper.selectOne(query);
        if (imGroupEntity == null) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(imGroupEntity);
    }

    /**
     * 更新群信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO updateBaseGroupInfo(UpdateGroupReq req) {

        //1.判断群id是否存在
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
        query.eq("group_id", req.getGroupId());
        query.eq("app_id", req.getAppId());
        ImGroupEntity imGroupEntity = imGroupDataMapper.selectOne(query);
        if (imGroupEntity == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_EXIST);
        }

        boolean isAdmin = false;

        // 管理员更新不需要校验权限信息
        if (!isAdmin) {
            //不是后台调用需要检查权限
            ResponseVO<GetRoleInGroupResp> role = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperate(), req.getAppId());
            if (!role.isOk()) {
                return role;
            }
            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();
            // 判断当前群成员是否是管理员或者群主
            boolean isManager = roleInfo == GroupMemberRoleEnum.MAMAGER.getCode() || roleInfo == GroupMemberRoleEnum.OWNER.getCode();

            //公开群只能群主修改资料
            if (!isManager && GroupTypeEnum.PUBLIC.getCode() == imGroupEntity.getGroupType()) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }
        ImGroupEntity update = new ImGroupEntity();
        BeanUtils.copyProperties(req, update);
        update.setUpdateTime(System.currentTimeMillis());
        int row = imGroupDataMapper.update(update, query);
        if (row != 1) {
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
        }

        // 更新之后回调通知
        UpdateGroupInfoPack updateGroupInfoPack = new UpdateGroupInfoPack();
        BeanUtils.copyProperties(update, updateGroupInfoPack);
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setAppId(req.getAppId());
        clientInfo.setImei(req.getImei());
        clientInfo.setClientType(req.getClientType());
        groupMessageProducer.sendMessage(req.getOperate(), req.getAppId(), imGroupEntity.getGroupId(), GroupEventCommand.UPDATED_GROUP, updateGroupInfoPack, clientInfo);

        if (appConfig.isModifyGroupAfterCallback()) {
            callbackService.callback(req.getAppId(), Constants.CallbackCommand.GROUP_UPDATE_AFTER,
                    JSON.toJSONString(imGroupDataMapper.selectOne(query)));
        }
        return ResponseVO.successResponse();
    }

    /**
     * 获取当前用户加入的群组信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO getJoinedGroup(GetJoinedGroupReq req) {
        ResponseVO<Collection<String>> memberJoinedGroup = imGroupMemberService.getMemberJoinedGroup(req);
        if (memberJoinedGroup.isOk()) {
            GetJoinedGroupResp resp = new GetJoinedGroupResp();

            if (CollectionUtils.isEmpty(memberJoinedGroup.getData())) {
                resp.setTotalCount(0);
                resp.setGroupList(new ArrayList<>());
                return ResponseVO.successResponse(resp);
            }

            // 查询所有的群信息
            QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
            query.eq("app_id", req.getAppId());
            query.in("group_id", memberJoinedGroup.getData());
            // 如果查询条件有群类型
            if (CollectionUtils.isNotEmpty(req.getGroupType())) {
                query.in("group_type", req.getGroupType());
            }
            List<ImGroupEntity> groupList = imGroupDataMapper.selectList(query);
            resp.setGroupList(groupList);
            // 如果存在分页限制
            if (req.getLimit() == null) {
                resp.setTotalCount(groupList.size());
            } else {
                resp.setTotalCount(imGroupDataMapper.selectCount(query));
            }
            return ResponseVO.successResponse(resp);
        } else {
            return memberJoinedGroup;
        }
    }

    @Override
    public ResponseVO destroyGroup(DestroyGroupReq req) {
        boolean isAdmin = false;
        // 查询群信息
        QueryWrapper<ImGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", req.getGroupId());
        queryWrapper.eq("app_id", req.getAppId());
        ImGroupEntity imGroupEntity = imGroupDataMapper.selectOne(queryWrapper);
        if (imGroupEntity == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        if (imGroupEntity.getStatus().equals(GroupStatusEnum.DESTROY.getCode())) {
            throw new ApplicationException(GroupErrorCode.THIS_GROUP_IS_DESTROY);
        }

        if (!isAdmin) {
            // 如果当前群是公开群，该群只有群主可以操作
            if (imGroupEntity.getGroupType() == GroupTypeEnum.PUBLIC.getCode() &&
                    !imGroupEntity.getOwnerId().equals(req.getOperate())) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }
        long redisSeqSeq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.GROUP_SEQ);
        ImGroupEntity update = new ImGroupEntity();
        update.setUpdateTime(System.currentTimeMillis());
        update.setStatus(GroupStatusEnum.DESTROY.getCode());
        update.setSequence(redisSeqSeq);
        int row = imGroupDataMapper.update(update, queryWrapper);
        if (row != 1) {
            throw new ApplicationException(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
        }

        // 注销之后的群成员通知
        DestroyGroupPack destroyGroupPack = new DestroyGroupPack();
        BeanUtils.copyProperties(update, destroyGroupPack);
        // 更新之后回调通知
        UpdateGroupInfoPack updateGroupInfoPack = new UpdateGroupInfoPack();
        BeanUtils.copyProperties(update, updateGroupInfoPack);
        updateGroupInfoPack.setSequence(redisSeqSeq);
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setAppId(req.getAppId());
        clientInfo.setImei(req.getImei());
        clientInfo.setClientType(req.getClientType());
        groupMessageProducer.sendMessage(req.getOperate(), req.getAppId(), imGroupEntity.getGroupId(), GroupEventCommand.DESTROY_GROUP, updateGroupInfoPack, clientInfo);

        // 注销之后的回调
        if (appConfig.isModifyGroupAfterCallback()) {
            DestroyGroupCallbackDto dto = new DestroyGroupCallbackDto();
            dto.setGroupId(req.getGroupId());
            callbackService.callback(req.getAppId()
                    , Constants.CallbackCommand.GROUP_DESTROY_AFTER,
                    JSON.toJSONString(dto));
        }

        return ResponseVO.successResponse();
    }

    /**
     * 转让群
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO transferGroup(TransferGroupReq req) {
        // 判断群是否存在
        ResponseVO group = getGroup(req.getGroupId(), req.getAppId());
        if (!group.isOk()) {
            return group;
        }
        ImGroupEntity imGroupEntity = (ImGroupEntity) group.getData();
        if (!imGroupEntity.getOwnerId().equals(req.getOperate())) {
            return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }

        // 查询当前操作者的群角色
        ResponseVO<GetRoleInGroupResp> ownerGroupOne = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperate(), req.getAppId());
        if (!ownerGroupOne.isOk()) {
            return ownerGroupOne;
        }
        if (ownerGroupOne.getData().getRole() != GroupMemberRoleEnum.OWNER.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }

        // 判断该用户是否在群内
        ResponseVO<GetRoleInGroupResp> roleInGroupOne = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOwnerId(), req.getAppId());

        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }

        long redisSeqSeq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.GROUP_SEQ);
        // 变更群的所有者
        ImGroupEntity update = new ImGroupEntity();
        update.setUpdateTime(System.currentTimeMillis());
        update.setOwnerId(req.getOwnerId());
        update.setSequence(redisSeqSeq);
        QueryWrapper<ImGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", req.getGroupId());
        queryWrapper.eq("app_id", req.getAppId());
        int row = imGroupDataMapper.update(update, queryWrapper);
        if (row != 1) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_OWNER_IS_NOT_REMOVE);
        }
        imGroupMemberService.transferGroupMember(req.getOwnerId(), req.getGroupId(), req.getAppId());

        return ResponseVO.successResponse();
    }

    /**
     * 禁言群
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO muteGroup(MuteGroupReq req) {
        // 校验群是否存在
        ResponseVO<ImGroupEntity> groupResp = getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }

        boolean isAdmin = false;

        if (!isAdmin) {
            //不是后台调用需要检查权限,获取当前操作人员的权限
            ResponseVO<GetRoleInGroupResp> role = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperate(), req.getAppId());

            if (!role.isOk()) {
                return role;
            }
            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();

            // 是否是管理员或者群主
            boolean isManager = roleInfo == GroupMemberRoleEnum.MAMAGER.getCode() || roleInfo == GroupMemberRoleEnum.OWNER.getCode();

            //公开群只能群主修改资料（只有群主或者管理员才能禁言）
            if (!isManager) {
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }

        ImGroupEntity update = new ImGroupEntity();
        update.setSequence(redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.GROUP_SEQ));
        update.setMute(req.getMute());
        UpdateWrapper<ImGroupEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("group_id", req.getGroupId());
        wrapper.eq("app_id", req.getAppId());
        imGroupDataMapper.update(update, wrapper);
        return ResponseVO.successResponse();
    }

    /**
     * 表增量同步，并没有缓存群成员和群的 sequence 信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO syncJoinedGroupList(SyncReq req) {
        // 1、拉取数量限制
        if (req.getMaxLimit() > 100) {
            req.setMaxLimit(100);
        }
        // 2、获取当前用户加入的所有的群ID（排除已经退群的记录
        List<String> memberJoinedGroupIds = imGroupMemberService.getMemberJoinedGroupByCondition(req.getAppId(), req.getOperate(), GroupMemberRoleEnum.LEAVE.getCode());
        // 返回值
        SyncResp<ImGroupEntity> resp = new SyncResp<>();
        if (!memberJoinedGroupIds.isEmpty()) {
            // 3、查询大于 sequence 的群组信息
            QueryWrapper<ImGroupEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_id", req.getAppId());
            queryWrapper.in("group_id", memberJoinedGroupIds);
            queryWrapper.gt("sequence", req.getLastSequence());
            queryWrapper.last(" limit " + req.getMaxLimit());
            queryWrapper.orderByAsc("sequence");
            List<ImGroupEntity> imGroupEntities = imGroupDataMapper.selectList(queryWrapper);
            if (!imGroupEntities.isEmpty()) {
                // 4、获取数据库中最大的群组 sequence
                ImGroupEntity maxGroupEntity = imGroupEntities.get(imGroupEntities.size() - 1);
                resp.setDataList(imGroupEntities);
                // 5、获取库中最大的 sequence
                Long maxSeq = imGroupDataMapper.getGroupMaxSeq(memberJoinedGroupIds, req.getAppId());
                resp.setCompleted(Objects.equals(maxSeq, maxGroupEntity.getSequence()));
                resp.setMaxSequence(maxSeq);
                return ResponseVO.successResponse(resp);
            }
        }
        resp.setMaxSequence(0L);
        resp.setCompleted(true);
        resp.setDataList(Collections.EMPTY_LIST);
        return ResponseVO.successResponse(resp);
    }

    /**
     * 获取当前用户加入的群组中最大的 seq
     *
     * @param appId
     * @param userId
     * @return
     */
    @Override
    public Long getMaxUserGroupSeq(Integer appId, String userId) {
        // 2、获取当前用户加入的所有的群ID（排除已经退群的记录
        List<String> memberJoinedGroupIds = imGroupMemberService.getMemberJoinedGroupByCondition(appId, userId, GroupMemberRoleEnum.LEAVE.getCode());
        if (memberJoinedGroupIds.isEmpty()) {
            return 0L;
        }
        return imGroupDataMapper.getGroupMaxSeq(memberJoinedGroupIds, appId);
    }
}
