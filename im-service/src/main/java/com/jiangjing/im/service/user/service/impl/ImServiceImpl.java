package com.jiangjing.im.service.user.service.impl;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.config.AppConfig;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.DelFlagEnum;
import com.jiangjing.im.common.enums.UserErrorCode;
import com.jiangjing.im.common.enums.command.UserEventCommand;
import com.jiangjing.im.common.exception.ApplicationException;
import com.jiangjing.im.service.group.service.ImGroupService;
import com.jiangjing.im.service.user.dao.ImUserDataEntity;
import com.jiangjing.im.service.user.dao.mapper.ImUserDataMapper;
import com.jiangjing.im.service.user.model.req.*;
import com.jiangjing.im.service.user.model.resp.GetUserInfoResp;
import com.jiangjing.im.service.user.model.resp.ImportUserResp;
import com.jiangjing.im.service.user.service.ImUserService;
import com.jiangjing.im.service.utils.CallbackService;
import com.jiangjing.im.service.utils.MessageProducer;
import com.jiangjing.pack.user.UserModifyPack;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: jianjing
 * @version: 1.0
 */
@Service
@Transactional
public class ImServiceImpl implements ImUserService {

    @Autowired
    ImUserDataMapper imUserDataMapper;

    @Autowired
    AppConfig appConfig;

    @Autowired
    CallbackService callbackService;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ImGroupService imGroupService;

    @Autowired
    NacosDiscoveryProperties nacosDiscoveryProperties;

    NamingService naming;

    @PostConstruct
    public void init() throws NacosException {
        naming = NamingFactory.createNamingService(nacosDiscoveryProperties.getServerAddr());
    }

    @Override
    public ResponseVO importUser(ImportUserReq req) {

        //判断导入的条数
        if (req.getUserData().size() > 100) {
            return ResponseVO.errorResponse(UserErrorCode.IMPORT_SIZE_BEYOND);
        }

        ImportUserResp resp = new ImportUserResp();
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        for (ImUserDataEntity data :
                req.getUserData()) {
            try {
                data.setAppId(req.getAppId());
                int insert = imUserDataMapper.insert(data);
                if (insert == 1) {
                    successId.add(data.getUserId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorId.add(data.getUserId());
            }
        }

        resp.setErrorId(errorId);
        resp.setSuccessId(successId);
        return ResponseVO.successResponse(resp);
    }

    /**
     * 批量删除用户
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO deleteUser(DeleteUserReq req) {

        List<String> errorId = new ArrayList<>();
        List<String> successId = new ArrayList<>();

        ImUserDataEntity imUserDataEntity = new ImUserDataEntity();
        imUserDataEntity.setDelFlag(DelFlagEnum.DELETE.getCode());
        req.getUserId().stream().forEach(userId -> {
            QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<ImUserDataEntity>();
            queryWrapper.eq("app_id", req.getAppId());
            queryWrapper.eq("user_id", userId);
            queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
            int update = 0;
            try {
                /**
                 * imUserDataEntity 设置需要变更的字段值， queryWrapper 设置where条件
                 */
                update = imUserDataMapper.update(imUserDataEntity, queryWrapper);
                if (update > 0) {
                    successId.add(userId);
                } else {
                    errorId.add(userId);
                }
            } catch (Exception e) {
                errorId.add(userId);

            }
        });
        ImportUserResp resp = new ImportUserResp();
        resp.setErrorId(errorId);
        resp.setSuccessId(successId);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO getUserInfo(GetUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId());
        queryWrapper.in("user_id", req.getUserIds());
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
        //根据查询条件返回 User 的 List 集合
        List<ImUserDataEntity> userDataEntities = imUserDataMapper.selectList(queryWrapper);
        //优化：没有必要使用 map 直接使用 filter 其实就可以
        HashMap<String, ImUserDataEntity> map = new HashMap<>();
        userDataEntities.forEach(imUserDataEntity -> map.put(imUserDataEntity.getUserId(), imUserDataEntity));
        //返回查询不到的 userId
        List<String> failUser = new ArrayList<>();
        req.getUserIds().forEach(item -> {
            if (!map.containsKey(item)) {
                failUser.add(item);
            }
        });
        GetUserInfoResp resp = new GetUserInfoResp();
        resp.setUserDataItem(userDataEntities);
        resp.setFailUser(failUser);
        return ResponseVO.successResponse(resp);
    }

    /**
     * 获取单个用户
     *
     * @param userId
     * @param appId
     * @return
     */
    @Override
    public ResponseVO getSingleUserInfo(String userId, Integer appId) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId);
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
        ImUserDataEntity imUserDataEntity = imUserDataMapper.selectOne(queryWrapper);
        if (imUserDataEntity == null) {
            return ResponseVO.errorResponse(UserErrorCode.USER_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(imUserDataEntity);
    }

    /**
     * 根据 userid 和 appid 修改用户信息
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO modifyUserInfo(ModifyUserInfoReq req) {
        QueryWrapper query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("user_id", req.getUserId());
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());
        ImUserDataEntity user = imUserDataMapper.selectOne(query);
        //判断用户是否存在
        if (user == null) {
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }
        ImUserDataEntity update = new ImUserDataEntity();
        BeanUtils.copyProperties(req, update);
        update.setAppId(null);
        update.setUserId(null);
        int update1 = imUserDataMapper.update(update, query);
        if (update1 == 1) {
            // 用户信息的修改的消息发送 - 登录端之间的数据同步
            UserModifyPack userModifyPack = new UserModifyPack();
            BeanUtils.copyProperties(req, userModifyPack);
            // 需要判断是否是管理员发送，还是用户本身去修改
            messageProducer.sendToUserByConditions(req.getUserId(), req.getAppId(), req.getClientType(), req.getImei(), UserEventCommand.USER_MODIFY, userModifyPack);

            // 修改成功，需要其他的业务逻辑，修改成功之后回调
            if (appConfig.isModifyUserAfterCallback()) {
                callbackService.callback(req.getAppId(), Constants.CallbackCommand.MODIFY_USER_AFTER, JSONObject.toJSONString(req));
            }
            return ResponseVO.successResponse();
        }
        throw new ApplicationException(UserErrorCode.MODIFY_USER_ERROR);
    }

    /**
     * 登录逻辑，空实现
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO login(LoginReq req) {
        return ResponseVO.successResponse();
    }

    /**
     * 获取当前用户的 sequence 信息，根据返回的 sequence 信息判断是否需要拉取信息，进行增量信息同步
     *
     * @param req
     * @return
     */
    @Override
    public ResponseVO getUserSequence(GetUserSequenceReq req) {
        // 1、获取缓存中的 sequence 信息（包含了 好友关系和会话信息）
        Map entries = redisTemplate.opsForHash().entries(req.getAppId() + Constants.RedisConstants.SEQ_PREFIX + req.getUserId());
        // 2、查询群组的 sequence 信息
        Long maxGroupSeq = imGroupService.getMaxUserGroupSeq(req.getAppId(), req.getUserId());
        // 3、组装返回值
        entries.put("maxGroupSeq", maxGroupSeq);
        return ResponseVO.successResponse(entries);
    }

    /**
     * 获取一个健康的实例节点
     *
     * @param serviceName
     * @return
     */
    @Override
    public Instance selectOneHealthyInstance(String serviceName) {
        try {
            return naming.selectOneHealthyInstance(serviceName);
        } catch (Exception e) {
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
    }
}
