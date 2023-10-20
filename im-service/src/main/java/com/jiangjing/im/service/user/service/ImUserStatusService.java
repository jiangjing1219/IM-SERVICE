package com.jiangjing.im.service.user.service;

import com.jiangjing.im.service.user.model.UserStatusChangeNotifyContent;
import com.jiangjing.im.service.user.model.req.PullFriendOnlineStatusReq;
import com.jiangjing.im.service.user.model.req.PullUserOnlineStatusReq;
import com.jiangjing.im.service.user.model.req.SetUserCustomerStatusReq;
import com.jiangjing.im.service.user.model.req.SubscribeUserOnlineStatusReq;
import com.jiangjing.im.service.user.model.resp.UserOnlineStatusResp;

import java.util.Map;

public interface ImUserStatusService {
    /**
     * 接收到用户在线状态改变，同步给其他客户端端，通知其他用户
     *
     * @param content
     */
    void processUserOnlineStatusNotify(UserStatusChangeNotifyContent content);

    /**
     * 临时订阅指定用户的在线状态
     *
     * @param req
     */
    void subscribeUserOnlineStatus(SubscribeUserOnlineStatusReq req);

    /**
     * 设置自定义在线状态
     *
     * @param req
     */
    void setUserCustomerStatus(SetUserCustomerStatusReq req);

    /**
     * 获取好友在线状态信息
     *
     * @param req
     * @return
     */
    Map<String, UserOnlineStatusResp> queryFriendOnlineStatus(PullFriendOnlineStatusReq req);

    /**
     * 获取指定用户的在线状态信息
     *
     * @param req
     * @return
     */
    Map<String, UserOnlineStatusResp> queryUserOnlineStatus(PullUserOnlineStatusReq req);
}
