package com.jiangjing.im.service.friendship.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangjing.im.common.model.SyncReq;
import com.jiangjing.im.service.friendship.dao.ImFriendShipEntity;
import com.jiangjing.im.service.friendship.model.req.CheckFriendShipReq;
import com.jiangjing.im.service.friendship.model.resp.CheckFriendShipResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author jingjing
 * @date 2023/5/3 23:22
 */
@Mapper
public interface ImFriendShipMapper extends BaseMapper<ImFriendShipEntity> {
    /**
     * 单向校验
     *
     * @param req
     * @return
     */
    @Select("<script>" +
            "select from_id as fromId , to_id as toId ,if(status = 1,1,0) as status from im_friendship where from_id = #{fromId} and to_id in " +
            "<foreach collection='toIds' index='index' item='id' separator=',' close = ')' open='(' > " +
            "#{id}" +
            "</foreach>" +
            "</script>")
    public List<CheckFriendShipResp> checkFriendShip(CheckFriendShipReq req);

    /**
     * 双向校验
     *
     * @param req
     * @return
     */
    @Select("<script>" +
            " select a.fromId,a.toId , ( \n" +
            " case \n" +
            " when a.status = 1 and b.status = 1 then 1 \n" +
            " when a.status = 1 and b.status != 1 then 2 \n" +
            " when a.status != 1 and b.status = 1 then 3 \n" +
            " when a.status != 1 and b.status != 1 then 4 \n" +
            " end \n" +
            " ) \n " +
            " as status from " +
            " (select from_id AS fromId , to_id AS toId , if(status = 1,1,0) as status from im_friendship where app_id = #{appId} and from_id = #{fromId} AND to_id in " +
            "<foreach collection='toIds' index='index' item='id' separator=',' close=')' open='('>" +
            " #{id} " +
            "</foreach>" +
            " ) as a INNER join" +
            " (select from_id AS fromId, to_id AS toId , if(status = 1,1,0) as status from im_friendship where app_id = #{appId} and to_id = #{fromId} AND from_id in " +
            "<foreach collection='toIds' index='index' item='id' separator=',' close=')' open='('>" +
            " #{id} " +
            "</foreach>" +
            " ) as b " +
            " on a.fromId = b.toId AND b.fromId = a.toId " +
            "</script>"
    )
    List<CheckFriendShipResp> checkFriendShipBoth(CheckFriendShipReq req);

    /**
     * 单向校验拉黑状态
     *
     * @param req
     * @return
     */
    @Select("<script>" +
            "select from_id as fromId , to_id as toId ,if(black = 1,1,0) as status from im_friendship where from_id = #{fromId} and to_id in " +
            "<foreach collection='toIds' index='index' item='id' separator=',' close = ')' open='(' > " +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<CheckFriendShipResp> checkFriendShipBlack(CheckFriendShipReq req);

    /**
     * 双向校验好友状态
     *
     * @param req
     * @return
     */
    @Select("<script>" +
            " select a.fromId,a.toId , ( \n" +
            " case \n" +
            " when a.black = 1 and b.black = 1 then 1 \n" +
            " when a.black = 1 and b.black != 1 then 2 \n" +
            " when a.black != 1 and b.black = 1 then 3 \n" +
            " when a.black != 1 and b.black != 1 then 4 \n" +
            " end \n" +
            " ) \n " +
            " as status from " +
            " (select from_id AS fromId , to_id AS toId , if(black = 1,1,0) as black from im_friendship where app_id = #{appId} and from_id = #{fromId} AND to_id in " +
            "<foreach collection='toIds' index='index' item='id' separator=',' close=')' open='('>" +
            " #{id} " +
            "</foreach>" +
            " ) as a INNER join" +
            " (select from_id AS fromId, to_id AS toId , if(black = 1,1,0) as black from im_friendship where app_id = #{appId} and to_id = #{fromId} AND from_id in " +
            "<foreach collection='toIds' index='index' item='id' separator=',' close=')' open='('>" +
            " #{id} " +
            "</foreach>" +
            " ) as b " +
            " on a.fromId = b.toId AND b.fromId = a.toId " +
            "</script>"
    )
    List<CheckFriendShipResp> checkFriendShipBlackBoth(CheckFriendShipReq req);

    /**
     * 获取当前用户好友关系链最大的 seq
     *
     * @param appId
     * @param userId
     * @return
     */
    @Select(" select max(friend_sequence) from im_friendship where app_id = #{appId} AND from_id = #{userId} ")
    Long getFriendShipMaxSeq(Integer appId, String userId);


    /**
     * 获取当前用户的所有好友的id
     *
     * @param appId
     * @param userId
     * @return
     */
    @Select(" select to_id from im_friendship where from_id = #{userId} AND app_id = #{appId} and status = 1 and black = 1 ")
    List<String> getAllFriendId(Integer appId, String userId);

    @Select("<script>" +
            "SELECT a.app_id, a.from_id, a.to_id, a.remark, a.status, a.black, a.create_time, a.friend_sequence, a.black_sequence, a.add_source, a.extra, b.nick_name " +
            "FROM im_friendship as a LEFT JOIN im_user_data as b on a.to_id = b.user_id " +
            "WHERE a.app_id = #{appId} AND a.from_id = #{fromId}" +
            "</script>" )
    List<ImFriendShipEntity> getAllFriendShip(Integer appId, String fromId);

    /**
     * 同步好友关系
     * @param req
     * @return
     */
    List<ImFriendShipEntity> syncFriendshipList(SyncReq req);
}
