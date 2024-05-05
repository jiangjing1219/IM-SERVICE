package com.jiangjing.im.service.user.controller;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.constant.Constants;
import com.jiangjing.im.common.enums.ClientType;
import com.jiangjing.im.common.route.RouteHandle;
import com.jiangjing.im.common.route.RouteInfo;
import com.jiangjing.im.service.user.model.req.*;
import com.jiangjing.im.service.user.service.ImUserService;
import com.jiangjing.im.service.user.service.ImUserStatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author:
 * @version: 1.0
 */
@RestController
@RequestMapping("v1/user")
@Api(tags = "ImUserController测试接口")
public class ImUserController {

    @Autowired
    ImUserService imUserService;

    @Autowired
    RouteHandle routeHandle;

    @Autowired
    ImUserStatusService imUserStatusService;


    /**
     * 批量导入用户信息，主要包含的信息有 appid 、 userid
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("importUser")
    @ApiOperation(value = "批量导入用户接口", notes = "批量导入")
    public ResponseVO importUser(@RequestBody ImportUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.importUser(req);
    }


    /**
     * 批量删除用户信息
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/deleteUser")
    @ApiOperation(value = "批量删除用户接口", notes = "批量删除")
    public ResponseVO deleteUser(@RequestBody @Validated DeleteUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.deleteUser(req);
    }

    /**
     * 实现登录逻辑，涉及到 netty ，需要确定要在哪台netty实例，返回 im 的地址
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/login")
    public ResponseVO login(@RequestBody @Validated LoginReq req, Integer appId) {
        req.setAppId(appId);
        ResponseVO login = imUserService.login(req);
        if (login.isOk()) {
            //获取 zk 上所有的im服务的地址
            Instance instance = null;
            if (ClientType.WEB.getCode() == req.getClientType()) {
                // web 端的登录，获取 webSocket 的地址
                instance = imUserService.selectOneHealthyInstance(Constants.IM_NACOS_SERVICE_WEB);
            } else {
                // 移动、pc 端的获取 tcp 链接地址
                instance = imUserService.selectOneHealthyInstance(Constants.IM_NACOS_SERVICE_TCP);
            }
            //根据配置的路由算法，返回 im 的链接地址
            return ResponseVO.successResponse(new RouteInfo(instance.getIp(), instance.getPort()));
        }
        return ResponseVO.errorResponse();
    }

    /**
     * 获取当前用户相关的 sequence
     * 1、好友关系链
     * 2、会话
     * 3、好友分组
     * 4、群组列表（没有保存到 redis ，需要实时查询数据库）
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/getUserSequence")
    public ResponseVO getUserSequence(@RequestBody @Validated GetUserSequenceReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.getUserSequence(req);
    }

    /**
     * 临时订阅用户的在线状态
     * 实现：使用 Redis 的 hash 结构， userid  subUserId  experienceTime
     *  todo 存在的问题，hash 结构的数据不支持对 field 设置过期时间所以超时需要手动删除
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/subscribeUserOnlineStatus")
    public ResponseVO subscribeUserOnlineStatus(@RequestBody @Validated SubscribeUserOnlineStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        imUserStatusService.subscribeUserOnlineStatus(req);
        return ResponseVO.successResponse();
    }

    /**
     * 用户设置自定义在线状态
     * 1、修改redis中的值
     * 2、同步登录端、通知订阅端
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/setUserCustomerStatus")
    public ResponseVO setUserCustomerStatus(@RequestBody @Validated SetUserCustomerStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        imUserStatusService.setUserCustomerStatus(req);
        return ResponseVO.successResponse();
    }

    /**
     * 获取好友的在线状态信息
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/queryFriendOnlineStatus")
    public ResponseVO queryFriendOnlineStatus(@RequestBody @Validated PullFriendOnlineStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        req.setUserId(identifier);
        return ResponseVO.successResponse(imUserStatusService.queryFriendOnlineStatus(req));
    }

    /**
     * 获取指定用户的在线状态信息
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/queryUserOnlineStatus")
    public ResponseVO queryUserOnlineStatus(@RequestBody @Validated PullUserOnlineStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperate(identifier);
        return ResponseVO.successResponse(imUserStatusService.queryUserOnlineStatus(req));
    }

}
