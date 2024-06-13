package com.jiangjing.im.service.user.service;


import com.alibaba.nacos.api.naming.pojo.Instance;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.service.user.dao.ImUserDataEntity;
import com.jiangjing.im.service.user.model.req.*;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
public interface ImUserService {

    /**
     * 批量导入用户信息
     *
     * @param req
     * @return
     */
    public ResponseVO importUser(ImportUserReq req);

    /**
     * 批量删除用户
     *
     * @param req
     * @return
     */
    ResponseVO deleteUser(DeleteUserReq req);

    /**
     * 批量获取用户信息
     *
     * @param req
     * @return
     */
    ResponseVO getUserInfo(GetUserInfoReq req);

    /**
     * 获取单个用户
     *
     * @param userId
     * @param appId
     * @return
     */
    ResponseVO getSingleUserInfo(String userId, Integer appId);

    /**
     * 修改用户信息
     *
     * @param req
     * @return
     */
    ResponseVO modifyUserInfo(ModifyUserInfoReq req);

    /**
     * 登录
     *
     * @param req
     * @return
     */
    ResponseVO login(LoginReq req);

    /**
     * 获取当前用户的额  sequence 信息
     *
     * @param req
     * @return
     */
    ResponseVO getUserSequence(GetUserSequenceReq req);

    /**
     * 根据负载均衡算法获取一个健康的实例节点
     *
     * @param serviceName
     * @return
     */
    Instance selectOneHealthyInstance(String serviceName);

    /**
     * 用户的模糊分页查询
     *
     * @param queryUserPageReq
     * @return
     */
    ResponseVO<IPage<ImUserDataEntity>> queryUserPage(QueryUserPageReq queryUserPageReq);

}
