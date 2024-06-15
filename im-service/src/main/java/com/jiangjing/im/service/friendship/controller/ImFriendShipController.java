package com.jiangjing.im.service.friendship.controller;

import com.jiangjing.im.common.ResponseVO;
import com.jiangjing.im.common.model.SyncReq;
import com.jiangjing.im.service.friendship.model.req.*;
import com.jiangjing.im.service.friendship.service.ImFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户关系的维护：
 * 1、强关系模型（微信好友，A 、 B 互为好友，在表设计的的时候需要插入 A - B、 B - A 这样的冗余对应关系，便于发数据量时的sql查询）‘
 * 2、弱关系模型（微博的关注模型，维护一个当前用户的一个关系列表即可，redis 使用 List 数据类型）
 * <p>
 * 目前我们需要实现的是强关系模型
 * <p>
 * 同步好友关系列表：目前是采用增量同步，在操作好友关系的 crud 时已经维护好 sequence，只需要好拉取增量的 sequence 数据做本地同步即可
 * 分页查询
 * 1、传统的分页查询，pageSize 、currentPage、totalCount 作为获取分页数据的必要参数，
 * 2、为了适配增量查询，采用的新的分页查询方式：对于 qpp 拉取数据列表来说，是不关心当前分页的，所以直接舍弃 currentPage 这个参数，app 端维护当前业务的最大 sequence ,每次拉取完数据就会将新数据的最大sequence当作app端的最大sequence，请求数据时会携带app端的sequence作为参数。服务端会判断参数的最大sequence和数据库的中的 sequence 是否一致，来返回 isComponent 是否拉取完成的标识。（可以实现只拉取增量数据）
 *
 * @author jingjing
 * @date 2023/5/3 17:56
 */
@RestController
@RequestMapping("v1/friendship")
public class ImFriendShipController {

    @Autowired
    ImFriendService imFriendShipService;


    /**
     * 对于一个社交系统来说，最总要的就是用户关系模型。
     * 批量导入第三方系统用户关系
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/importFriendShip")
    public ResponseVO importFriendShip(@RequestBody @Validated ImporFriendShipReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.importFriendShip(req);
    }

    /**
     * 添加好友，一次只添加一个
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/addFriend")
    public ResponseVO addFriend(@RequestBody @Validated AddFriendReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.addFriend(req);
    }

    /**
     * 更新好友信息
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/updateFriend")
    public ResponseVO updateFriend(@RequestBody @Validated UpdateFriendReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.updateFriend(req);
    }

    /**
     * 删除指定的好友
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/deleteFriend")
    public ResponseVO deleteFriend(@RequestBody @Validated DeleteFriendReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.deleteFriend(req);
    }

    /**
     * 删除当前用户所有的好友
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/deleteAllFriend")
    public ResponseVO deleteAllFriend(@RequestBody @Validated DeleteFriendReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.deleteAllFriend(req);
    }

    /**
     * 获取当前用户所有的好友信息
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/getAllFriendShip")
    public ResponseVO getAllFriendShip(@RequestBody @Validated GetAllFriendShipReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.getAllFriendShip(req);
    }

    /**
     * 获取指定好友的好友信息
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/getRelation")
    public ResponseVO getRelation(@RequestBody @Validated GetRelationReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.getRelation(req);
    }

    /**
     * 校验好友状态 （单向叫校验 / 双向校验）
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/checkFriend")
    public ResponseVO checkFriend(@RequestBody @Validated CheckFriendShipReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.checkFriendship(req);
    }


    /**
     * 拉黑
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/addBlack")
    public ResponseVO addBlack(@RequestBody @Validated AddFriendShipBlackReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.addBlack(req);
    }

    /**
     * 删除拉黑
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/deleteBlack")
    public ResponseVO deleteBlack(@RequestBody @Validated DeleteBlackReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.deleteBlack(req);
    }

    /**
     * 单向校验来黑状态，from_id 的关系为返回值
     * 双向校验拉黑状态
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/checkBlck")
    public ResponseVO checkBlack(@RequestBody @Validated CheckFriendShipReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.checkBlack(req);
    }


    /**
     * 好友关系同步（使用 sequence 实现增量同步）
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/syncFriendshipList")
    public ResponseVO syncFriendshipList(@RequestBody @Validated SyncReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipService.syncFriendshipList(req);
    }

}
