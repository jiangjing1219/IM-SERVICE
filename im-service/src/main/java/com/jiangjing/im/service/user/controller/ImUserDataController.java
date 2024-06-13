package com.jiangjing.im.service.user.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.service.user.dao.ImUserDataEntity;
import com.jiangjing.im.service.user.model.req.GetUserInfoReq;
import com.jiangjing.im.service.user.model.req.ModifyUserInfoReq;
import com.jiangjing.im.service.user.model.req.QueryUserPageReq;
import com.jiangjing.im.service.user.model.req.UserId;
import com.jiangjing.im.service.user.service.ImUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取用户信息
 *
 * @author Admin
 */
@RestController
@RequestMapping("v1/user/data")
public class ImUserDataController {

    @Autowired
    ImUserService imUserService;


    /**
     * 批量获取用户
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/getUserInfo")
    public ResponseVO getUserInfo(@RequestBody GetUserInfoReq req, Integer appId) {//@Validated
        req.setAppId(appId);
        return imUserService.getUserInfo(req);
    }

    /**
     * 获取单个用户
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/getSingleUserInfo")
    public ResponseVO getSingleUserInfo(@RequestBody @Validated UserId req, Integer appId) {
        req.setAppId(appId);
        return imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
    }

    /**
     * 修改单个用户信息
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/modifyUserInfo")
    public ResponseVO modifyUserInfo(@RequestBody @Validated ModifyUserInfoReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.modifyUserInfo(req);
    }

    @RequestMapping("/queryImUserPage")
    public ResponseVO<IPage<ImUserDataEntity>> queryUserPage(@RequestBody @Validated QueryUserPageReq queryUserPageReq) {
        return imUserService.queryUserPage(queryUserPageReq);
    }

}
