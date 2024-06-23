package com.jiangjing.im.service.friendship.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.config.AppConfig;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.AllowFriendTypeEnum;
import com.jiangjing.im.common.enums.CheckFriendShipTypeEnum;
import com.jiangjing.im.common.enums.FriendShipErrorCode;
import com.jiangjing.im.common.enums.FriendShipStatusEnum;
import com.jiangjing.im.common.enums.command.FriendshipEventCommand;
import com.jiangjing.im.common.exception.ApplicationException;
import com.jiangjing.im.common.model.RequestBase;
import com.jiangjing.im.common.model.SyncReq;
import com.jiangjing.im.common.model.SyncResp;
import com.jiangjing.im.service.friendship.dao.ImFriendShipEntity;
import com.jiangjing.im.service.friendship.dao.mapper.ImFriendShipMapper;
import com.jiangjing.im.service.friendship.model.callback.AddFriendAfterCallbackDto;
import com.jiangjing.im.service.friendship.model.callback.AddFriendBlackAfterCallbackDto;
import com.jiangjing.im.service.friendship.model.callback.DeleteFriendAfterCallbackDto;
import com.jiangjing.im.service.friendship.model.req.*;
import com.jiangjing.im.service.friendship.model.resp.CheckFriendShipResp;
import com.jiangjing.im.service.friendship.model.resp.ImportFriendShipResp;
import com.jiangjing.im.service.friendship.service.ImFriendService;
import com.jiangjing.im.service.friendship.service.ImFriendShipRequestService;
import com.jiangjing.im.service.sequence.RedisSeq;
import com.jiangjing.im.service.sequence.WriteUserSeq;
import com.jiangjing.im.service.user.dao.ImUserDataEntity;
import com.jiangjing.im.service.user.service.ImUserService;
import com.jiangjing.im.service.utils.CallbackService;
import com.jiangjing.im.service.utils.MessageProducer;
import com.jiangjing.pack.friendship.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author jingjing
 * @date 2023/5/3 23:30
 */

@Service
@Transactional
public class ImFriendServiceImpl implements ImFriendService {

    @Autowired
    ImFriendShipMapper imFriendShipMapper;

    @Autowired
    ImUserService imUserService;

    @Autowired
    ImFriendShipRequestService imFriendShipRequestService;

    @Autowired
    AppConfig appConfig;

    @Autowired
    CallbackService callbackService;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    WriteUserSeq writeUserSeq;

    @Autowired
    RedisSeq redisSeq;


    /**
     * 批量导入好友关系
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO importFriendShip(ImporFriendShipReq req) {

        // 限制一次导入的条数
        if (req.getFriendItem().size() > 100) {
            return ResponseVO.errorResponse(FriendShipErrorCode.IMPORT_SIZE_BEYOND);
        }

        ImportFriendShipResp resp = new ImportFriendShipResp();
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        req.getFriendItem().forEach(item -> {
            // 组装需要导入的用户关系对象信息
            ImFriendShipEntity imFriendShipEntity = new ImFriendShipEntity();
            BeanUtils.copyProperties(item, imFriendShipEntity);
            imFriendShipEntity.setAppId(req.getAppId());
            imFriendShipEntity.setFromId(req.getFromId());
            try {
                int insert = imFriendShipMapper.insert(imFriendShipEntity);
                if (insert == 1) {
                    successId.add(item.getToId());
                } else {
                    errorId.add(item.getToId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorId.add(item.getToId());
            }
        });
        resp.setErrorId(errorId);
        resp.setSuccessId(successId);
        return ResponseVO.successResponse(resp);
    }

    /**
     * 添加好友，一对一
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO addFriend(AddFriendReq req) {
        // 判断当前用户是否存在
        ResponseVO<ImUserDataEntity> fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }
        // 判断被添加的用户信息是否存在
        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }

        // 满足添加好友的条件之后，需要做 Before 回调
        if (appConfig.isAddFriendBeforeCallback()) {
            ResponseVO responseVO = callbackService.beforeCallback(req.getAppId(), Constants.CallbackCommand.ADD_FRIEND_BEFORE, JSON.toJSONString(req));
            if (!responseVO.isOk()) {
                return responseVO;
            }
        }
        // 取出被添加的用户信息
        ImUserDataEntity data = toInfo.getData();

        //判断添加用户是否需要验证
        if (data.getFriendAllowType() != null && data.getFriendAllowType() == AllowFriendTypeEnum.NOT_NEED.getCode()) {
            //不需要走验证，有人添加直接新增好友
            return this.doAddFriend(req, req.getFromId(), req.getToItem(), req.getAppId());
        } else {
            // 判断是否是好友关系
            QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
            query.eq("app_id", req.getAppId());
            query.eq("from_id", req.getFromId());
            query.eq("to_id", req.getToItem().getToId());
            ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
            if (fromItem == null || fromItem.getStatus() != FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                // 需要做好友申请
                ResponseVO responseVO = imFriendShipRequestService.addFriendshipRequest(req.getFromId(), req.getToItem(), req.getAppId());
                if (!responseVO.isOk()) {
                    // 添加失败，直接返回失败的结果
                    return responseVO;
                }
                return ResponseVO.successResponse().setMessage("已发送好友申请");
            } else {
                return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            }
        }
    }

    /**
     * 添加好友的逻辑：
     * 1、校验双方用户是否存在
     * 2、判断的是否需要好友验证
     * 需要，验证，需要验证通过之后才会调用到 doAddRFriend
     * 不需要验证，直接调用 doAddRFriend
     * 3、doAddRFriend 是正常直接好友添加的方法
     * 添加完成之后，需要做
     * 1、好友信息同步，给 发起添加的用户的其他客户端发送添加的好友信息，进行好友信息同步。被添加的用户的所有端发起信息同步
     * 2、添加成功之后的回调
     *
     * @param req
     * @param fromId
     * @param toItem
     * @param appId
     * @return
     */
    @Override
    public ResponseVO doAddFriend(RequestBase req, String fromId, FriendDto toItem, Integer appId) {
        // A - B ，A 添加 B 为好友
        // A 与 B 互为好友，应该添加两条记录
        // 查询是否存在还有记录，如果存在，需要判断当前的好友状态。已添加，提示已经是好友了，未添加，则修改好友状态


        // 一、A 添加 B 为好友
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", appId);
        query.eq("from_id", fromId);
        query.eq("to_id", toItem.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);

        long friendshipSeq = redisSeq.getSeq(appId + ":" + Constants.SeqConstants.FRIENDSHIP_SEQ);
        if (fromItem == null) {
            // 没有好友信息，走直接添加逻辑
            fromItem = new ImFriendShipEntity();
            BeanUtils.copyProperties(toItem, fromItem);
            fromItem.setAppId(appId);
            fromItem.setFromId(fromId);
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            fromItem.setBlack(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
            //添加好友序列
            fromItem.setFriendSequence(friendshipSeq);
            // 插入一条好友关系记录
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            //已经存在好友信息，判断相应的好友状态：如果是已添加，则提示已添加，如果是未添加，则修改状态
            if (fromItem.getStatus() == FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            }
            //更新好友状态
            if (fromItem.getStatus() == FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()) {

                ImFriendShipEntity update = new ImFriendShipEntity();
                //添加来源
                if (StringUtils.isNotBlank(toItem.getAddSource())) {
                    update.setAddSource(toItem.getAddSource());
                }
                // 备注
                if (StringUtils.isNotBlank(toItem.getRemark())) {
                    update.setRemark(toItem.getRemark());
                }
                // 验证信息
                if (StringUtils.isNotBlank(toItem.getExtra())) {
                    update.setExtra(toItem.getExtra());
                }
                // 好友状态设置为 正常
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                // 更新好友关系的序列号
                update.setFriendSequence(friendshipSeq);
                int result = imFriendShipMapper.update(update, query);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }

            }
        }
        // 更新 form 用户的序列缓存信息
        writeUserSeq.writeUserSeq(appId, fromId, Constants.SeqConstants.FRIENDSHIP_SEQ, friendshipSeq);

        //二、B 添加 A 为好友，调换 form_id 和 toId
        QueryWrapper<ImFriendShipEntity> toQuery = new QueryWrapper<>();
        toQuery.eq("app_id", appId);
        toQuery.eq("from_id", toItem.getToId());
        toQuery.eq("to_id", fromId);
        ImFriendShipEntity toItem_1 = imFriendShipMapper.selectOne(toQuery);
        if (toItem_1 == null) {
            toItem_1 = new ImFriendShipEntity();
            BeanUtils.copyProperties(toItem, toItem_1);
            toItem_1.setAppId(appId);
            toItem_1.setFromId(toItem.getToId());
            toItem_1.setToId(fromId);
            toItem_1.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            toItem_1.setCreateTime(System.currentTimeMillis());
            toItem_1.setBlack(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
            toItem_1.setFriendSequence(friendshipSeq);
            toItem_1.setRemark("");
            int insert = imFriendShipMapper.insert(toItem_1);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR, "B 添加 A 为好友失败");
            }
        } else {
            //已经存在好友信息，判断相应的好友状态：如果是已添加，则提示已添加，如果是未添加，则修改状态
            if (toItem_1.getStatus() == FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            }
            //更新好友状态
            if (toItem_1.getStatus() == FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()) {

                ImFriendShipEntity update = new ImFriendShipEntity();
                //添加来源
                if (StringUtils.isNotBlank(toItem_1.getAddSource())) {
                    update.setAddSource(toItem_1.getAddSource());
                }
                // 备注
                if (StringUtils.isNotBlank(toItem_1.getRemark())) {
                    update.setRemark(toItem_1.getRemark());
                }
                // 验证信息
                if (StringUtils.isNotBlank(toItem_1.getExtra())) {
                    update.setExtra(toItem_1.getExtra());
                }

                update.setFriendSequence(friendshipSeq);
                // 好友状态设置为 正常
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

                int result = imFriendShipMapper.update(update, query);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
            }
        }
        // 被添加者的维护好友关系的 seq
        writeUserSeq.writeUserSeq(appId, toItem.getToId(), Constants.SeqConstants.FRIENDSHIP_SEQ, friendshipSeq);

        // 好友添加完成之后，好友信息同步
        // 添加方，通知其他端
        AddFriendPack fromPack = new AddFriendPack();
        BeanUtils.copyProperties(fromItem, fromPack);
        fromPack.setSequence(friendshipSeq);
        messageProducer.sendToUserByConditions(fromItem.getFromId(), req.getAppId(), req.getClientType(), req.getImei(), FriendshipEventCommand.FRIEND_ADD, fromPack);
        // 被添加方，需要通知所有端
        AddFriendPack toPack = new AddFriendPack();
        BeanUtils.copyProperties(toItem_1, toPack);
        toPack.setSequence(friendshipSeq);
        messageProducer.sendToUserByAll(toItem_1.getFromId(), req.getAppId(), FriendshipEventCommand.FRIEND_ADD, toPack);


        // 添加完成之后回调，之后回调
        if (appConfig.isAddFriendAfterCallback()) {
            AddFriendAfterCallbackDto callbackDto = new AddFriendAfterCallbackDto();
            callbackDto.setFromId(fromId);
            callbackDto.setToItem(toItem);
            callbackService.callback(appId, Constants.CallbackCommand.ADD_FRIEND_AFTER, JSONObject.toJSONString(callbackDto));
        }
        return ResponseVO.successResponse().setMessage("添加好友成功！");
    }


    /**
     * 更新好友信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO updateFriend(UpdateFriendReq req) {

        ResponseVO<ImUserDataEntity> fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }
        // 插入好友关系的 seq
        long friendshipSeq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.FRIENDSHIP_SEQ);
        ResponseVO responseVO = this.doUpdate(req.getFromId(), req.getToItem(), req.getAppId(), friendshipSeq);

        if (responseVO.isOk()) {
            // 更新好友的客户端数据同步
            UpdateFriendPack updateFriendPack = new UpdateFriendPack();
            BeanUtils.copyProperties(req.getToItem(), updateFriendPack);
            updateFriendPack.setSequence(friendshipSeq);
            messageProducer.sendToUserByConditions(req.getFromId(), req.getAppId(), req.getClientType(), req.getImei(), FriendshipEventCommand.FRIEND_UPDATE, updateFriendPack);

            // 更新完成之后回调
            if (appConfig.isModifyFriendAfterCallback()) {
                AddFriendAfterCallbackDto callbackDto = new AddFriendAfterCallbackDto();
                callbackDto.setFromId(req.getFromId());
                callbackDto.setToItem(req.getToItem());
                callbackService.callback(req.getAppId(), Constants.CallbackCommand.UPDATE_FRIEND_AFTER, JSONObject.toJSONString(callbackDto));
            }
        }
        return responseVO;
    }

    /**
     * 删除指定的好友
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO deleteFriend(DeleteFriendReq req) {
        // 1、查询当前用户是否存在该好友信息
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("to_id", req.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
        if (fromItem == null) {
            // 不是好友
            return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        }
        // 2、当前用户的还有状态是否是正常状态
        if (fromItem.getStatus() != null && FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode() == fromItem.getStatus()) {
            // 3、如果是正常状态，那么执行状态更新操作
            ImFriendShipEntity update = new ImFriendShipEntity();
            update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
            // 4、 插入好友关系的序列
            long friendshipSeq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.FRIENDSHIP_SEQ);
            update.setFriendSequence(friendshipSeq);
            imFriendShipMapper.update(update, query);
            // 5、更新缓存
            writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FRIENDSHIP_SEQ, friendshipSeq);
            // 删除之后的客户端数据同步
            DeleteFriendPack deleteFriendPack = new DeleteFriendPack();
            deleteFriendPack.setFromId(req.getFromId());
            deleteFriendPack.setToId(req.getToId());
            deleteFriendPack.setSequence(friendshipSeq);
            messageProducer.sendToUserByConditions(req.getFromId(), req.getAppId(), req.getClientType(), req.getImei(), FriendshipEventCommand.FRIEND_DELETE, deleteFriendPack);

            //之后回调
            if (appConfig.isAddFriendAfterCallback()) {
                DeleteFriendAfterCallbackDto callbackDto = new DeleteFriendAfterCallbackDto();
                callbackDto.setFromId(req.getFromId());
                callbackDto.setToId(req.getToId());
                callbackService.callback(req.getAppId(), Constants.CallbackCommand.DELETE_FRIEND_AFTER, JSONObject.toJSONString(callbackDto));
            }
        } else {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
        }
        return ResponseVO.successResponse();
    }

    /**
     * 删除指定用户的所有好友
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO deleteAllFriend(DeleteFriendReq req) {
        // 1、查询出当前用户所有的好友
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("status", FriendShipStatusEnum.FRIEND_STATUS_NORMAL);
        // 2、执行更新操作
        ImFriendShipEntity update = new ImFriendShipEntity();
        long friendshipSeq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.FRIENDSHIP_SEQ);
        update.setFriendSequence(friendshipSeq);
        update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendShipMapper.update(update, query);

        // 删除时候的数据同步
        DeleteAllFriendPack deleteAllFriendPack = new DeleteAllFriendPack();
        deleteAllFriendPack.setFromId(req.getFromId());
        deleteAllFriendPack.setSequence(friendshipSeq);
        messageProducer.sendToUserByConditions(req.getFromId(), req.getAppId(), req.getClientType(), req.getImei(), FriendshipEventCommand.FRIEND_ALL_DELETE, deleteAllFriendPack);

        // 更新缓存 ———  redis 中缓存的了当前好友关系的最大 seq 信息，客户端在拉取消息是是需要要拉取这个 MaxSeq 和本地的 seq 之间的变更操作即可
        writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FRIENDSHIP_SEQ, friendshipSeq);
        return ResponseVO.successResponse();
    }

    /**
     * 更新好友信息
     *
     * @param fromId
     * @param toItem
     * @param appId
     * @return
     */
    private ResponseVO doUpdate(String fromId, FriendDto toItem, Integer appId, Long friendshipSeq) {
        UpdateWrapper<ImFriendShipEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ImFriendShipEntity::getAddSource, toItem.getAddSource()).set(ImFriendShipEntity::getExtra, toItem.getExtra()).set(ImFriendShipEntity::getRemark, toItem.getRemark()).set(ImFriendShipEntity::getFriendSequence, friendshipSeq).eq(ImFriendShipEntity::getAppId, appId).eq(ImFriendShipEntity::getToId, toItem.getToId()).eq(ImFriendShipEntity::getFromId, fromId);
        int update = imFriendShipMapper.update(null, updateWrapper);
        if (update != 1) {
            return ResponseVO.errorResponse();
        }
        // 更新缓存
        writeUserSeq.writeUserSeq(appId, fromId, Constants.SeqConstants.FRIENDSHIP_SEQ, friendshipSeq);
        return ResponseVO.successResponse();
    }


    /**
     * 获取当前用户所有的好友信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO getAllFriendShip(GetAllFriendShipReq req) {
        // 1、查询出当前用户所有的好友
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        List<ImFriendShipEntity> list =  imFriendShipMapper.getAllFriendShip(req.getAppId(), req.getFromId());
        return ResponseVO.successResponse(list);
    }


    /**
     * 获取指定好友的好友信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO getRelation(GetRelationReq req) {
        ImFriendShipEntity imFriendShipEntity = imFriendShipMapper.getRelationById(req);
        if (imFriendShipEntity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.REPEATSHIP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(imFriendShipEntity);
    }

    /**
     * 校验好友状态
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO checkFriendship(CheckFriendShipReq req) {

        Map<String, Integer> result = req.getToIds().stream().collect(Collectors.toMap(Function.identity(), s -> 0));

        List<CheckFriendShipResp> resp;

        if (req.getCheckType() == CheckFriendShipTypeEnum.SINGLE.getType()) {
            // 单向校验
            resp = imFriendShipMapper.checkFriendShip(req);
        } else {
            // 双向校验  使用 inner join 只能查出由记录的好友信息
            resp = imFriendShipMapper.checkFriendShipBoth(req);
        }

        Map<String, Integer> collect = resp.stream().collect(Collectors.toMap(CheckFriendShipResp::getToId, CheckFriendShipResp::getStatus));

        //  如果 to_id  = 888  并没有这条记录，那么就构造一个非好友的返回
        for (String toId : result.keySet()) {
            if (!collect.containsKey(toId)) {
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setFromId(req.getFromId());
                checkFriendShipResp.setToId(toId);
                checkFriendShipResp.setStatus(result.get(toId));
                resp.add(checkFriendShipResp);
            }
        }
        return ResponseVO.successResponse(resp);
    }

    /**
     * 拉黑用户
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO addBlack(AddFriendShipBlackReq req) {
        // 1、 查询当前用户记录，判断当前用户是否存在
        ResponseVO<ImUserDataEntity> fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }
        // 2、查询需要拉黑的用户是否存在
        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(req.getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }
        // 3、查询好友关系
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("to_id", req.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);

        long friendshipSeq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.FRIENDSHIP_SEQ);
        // 4、更新 black 字段
        if (fromItem == null) {
            //走添加逻辑。
            fromItem = new ImFriendShipEntity();
            fromItem.setFromId(req.getFromId());
            fromItem.setToId(req.getToId());
            fromItem.setAppId(req.getAppId());
            fromItem.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
            fromItem.setFriendSequence(friendshipSeq);
            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_BLACK_ERROR);
            }
        } else {
            // 5、更新拉黑字段（如果存在则判断状态，如果是拉黑，则提示已拉黑）
            //如果存在则判断状态，如果是拉黑，则提示已拉黑，如果是未拉黑，则修改状态
            if (fromItem.getBlack() != null && fromItem.getBlack() == FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_BLACK);
            } else {
                ImFriendShipEntity update = new ImFriendShipEntity();
                update.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
                update.setFriendSequence(friendshipSeq);
                int result = imFriendShipMapper.update(update, query);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_BLACK_ERROR);
                }
            }
        }
        // 6、更新好友序列缓存
        writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FRIENDSHIP_SEQ, friendshipSeq);

        // 拉黑之后的数据同步
        AddFriendBlackPack addFriendBlackPack = new AddFriendBlackPack();
        addFriendBlackPack.setFromId(req.getFromId());
        addFriendBlackPack.setToId(req.getToId());
        addFriendBlackPack.setSequence(friendshipSeq);
        messageProducer.sendToUserByConditions(req.getFromId(), req.getAppId(), req.getClientType(), req.getImei(), FriendshipEventCommand.FRIEND_BLACK_ADD, addFriendBlackPack);

        //之后回调
        if (appConfig.isAddFriendShipBlackAfterCallback()) {
            AddFriendBlackAfterCallbackDto callbackDto = new AddFriendBlackAfterCallbackDto();
            callbackDto.setFromId(req.getFromId());
            callbackDto.setToId(req.getToId());
            callbackService.callback(req.getAppId(), Constants.CallbackCommand.ADD_BLACK_AFTER, JSONObject.toJSONString(callbackDto));
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO deleteBlack(DeleteBlackReq req) {
        // 1、查询好友关系
        QueryWrapper<ImFriendShipEntity> queryFrom = new QueryWrapper<>();
        queryFrom.eq("from_id", req.getFromId()).eq("app_id", req.getAppId()).eq("to_id", req.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(queryFrom);
        // 2、判断当前好友关系是否是拉黑状态，如果是正常状态提示
        if (fromItem.getBlack() != null && fromItem.getBlack() == FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode()) {
            throw new ApplicationException(FriendShipErrorCode.FRIEND_IS_NOT_YOUR_BLACK);
        }
        // 3、更新拉黑状态
        ImFriendShipEntity update = new ImFriendShipEntity();
        // 4、添加好友序列信息
        long friendshipSeq = redisSeq.getSeq(req.getAppId() + ":" + Constants.SeqConstants.FRIENDSHIP_SEQ);
        update.setFriendSequence(friendshipSeq);
        update.setBlack(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
        int update1 = imFriendShipMapper.update(update, queryFrom);
        if (update1 == 1) {
            // 更新缓存
            writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FRIENDSHIP_SEQ, friendshipSeq);
            // 4、更新成功
            DeleteFriendPack deleteFriendPack = new DeleteFriendPack();
            deleteFriendPack.setFromId(req.getFromId());
            deleteFriendPack.setToId(req.getToId());
            // 5、客户端需要维护相关的最大值的 seq
            deleteFriendPack.setSequence(friendshipSeq);
            messageProducer.sendToUserByConditions(req.getToId(), req.getAppId(), req.getClientType(), req.getImei(), FriendshipEventCommand.FRIEND_BLACK_DELETE, deleteFriendPack);

            //之后回调
            if (appConfig.isAddFriendShipBlackAfterCallback()) {
                AddFriendBlackAfterCallbackDto callbackDto = new AddFriendBlackAfterCallbackDto();
                callbackDto.setFromId(req.getFromId());
                callbackDto.setToId(req.getToId());
                callbackService.callback(req.getAppId(), Constants.CallbackCommand.DELETE_BLACK, JSONObject.toJSONString(callbackDto));
            }
        }
        return ResponseVO.successResponse();
    }

    /**
     * 单向校验拉黑状态
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO checkBlack(CheckFriendShipReq req) {

        List<CheckFriendShipResp> resp;

        if (req.getCheckType() == CheckFriendShipTypeEnum.SINGLE.getType()) {
            // 单向校验
            resp = imFriendShipMapper.checkFriendShipBlack(req);
        } else {
            // 双向校验  使用 inner join 只能查出由记录的好友信息
            resp = imFriendShipMapper.checkFriendShipBlackBoth(req);
        }
        return ResponseVO.successResponse(resp);
    }

    /**
     * 同步好友关系列表
     * 1、采用增量查询的方式
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO syncFriendshipList(SyncReq req) {
        // 限制最大查询条数
        if (req.getMaxLimit() > 100) {
            req.setMaxLimit(100);
        }
        QueryWrapper<ImFriendShipEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("from_id", req.getOperate());
        queryWrapper.eq("app_id", req.getAppId());
        queryWrapper.gt("friend_sequence", req.getLastSequence());
        queryWrapper.last("limit " + req.getMaxLimit());
        queryWrapper.orderByAsc("friend_sequence");
        List<ImFriendShipEntity> friendShipEntityList = imFriendShipMapper.syncFriendshipList(req);
        // 组装返回值
        SyncResp<ImFriendShipEntity> resp = new SyncResp<>();
        if (!friendShipEntityList.isEmpty()) {
            // 当前分页最新的记录
            ImFriendShipEntity maxFriendShipEntity = friendShipEntityList.get(friendShipEntityList.size() - 1);
            resp.setDataList(friendShipEntityList);
            // 获取当前库中最新的记录 seq
            Long friendShipMaxSeq = imFriendShipMapper.getFriendShipMaxSeq(req.getAppId(), req.getOperate());
            resp.setCompleted(Objects.equals(friendShipMaxSeq, maxFriendShipEntity.getFriendSequence()));
            resp.setMaxSequence(friendShipMaxSeq);
            return ResponseVO.successResponse(resp);
        }
        // 查询不到记录，返回 true
        resp.setCompleted(true);
        resp.setDataList(Collections.EMPTY_LIST);
        resp.setMaxSequence(0L);
        return ResponseVO.successResponse(resp);
    }

    /**
     * 获取当前用户所有的好友id
     *
     * @param appId
     * @param userId
     * @return
     */
    @Override
    public List<String> getAllFriendId(Integer appId, String userId) {
        return imFriendShipMapper.getAllFriendId(appId, userId);
    }
}
