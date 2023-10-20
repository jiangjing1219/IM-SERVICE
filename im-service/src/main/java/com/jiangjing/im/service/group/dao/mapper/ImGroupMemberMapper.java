package com.jiangjing.im.service.group.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangjing.im.service.group.dao.ImGroupMemberEntity;
import com.jiangjing.im.service.group.model.req.GroupMemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author jingjing
 * @date 2023/5/25 0:20
 */
@Mapper
public interface ImGroupMemberMapper extends BaseMapper<ImGroupMemberEntity> {

    @Select("select " +
            " member_id, " +
            " speak_flag,  " +
            " speak_date,  " +
            " role, " +
            " alias, " +
            " join_time ," +
            " join_type " +
            " from im_group_member where app_id = #{appId} AND group_id = #{groupId} ")
    List<GroupMemberDto> getGroupMember(QueryWrapper<GroupMemberDto> queryWrapper);


    @Select("select group_id from im_group_member where app_id = #{appId} AND member_id = #{memberId} ")
    List<String> getJoinedGroupId(Integer appId, String memberId);


    @Select("select " +
            " member_id " +
            " from im_group_member where app_id = #{appId} AND group_id = #{groupId} and role != 3")
    List<String> getGroupMemberIds(Integer appId, String groupId);


    /**
     * 获取群管理员
     *
     * @param groupId
     * @param appId
     * @return
     */
    @Results({
            @Result(column = "member_id", property = "memberId"),
            @Result(column = "speak_flag", property = "speakFlag"),
            @Result(column = "role", property = "role"),
            @Result(column = "alias", property = "alias"),
            @Result(column = "join_time", property = "joinTime"),
            @Result(column = "join_type", property = "joinType")
    })
    @Select("select " +
            " member_id, " +
            " speak_flag,  " +
            " role " +
            " alias, " +
            " join_time ," +
            " join_type " +
            " from im_group_member where app_id = #{appId} AND group_id = #{groupId} and role in (1,2) ")
    List<GroupMemberDto> getGroupManagers(String groupId, Integer appId);

    /**
     * 获取当前用户加入的群组 ID ，排除指定的角色状态
     *
     * @param appId
     * @param userId
     * @param excludeRole
     * @return
     */
    @Select("select group_id from im_group_member where app_id = #{appId} AND member_id = #{userId} and role != #{excludeRole}" )
    List<String> getMemberJoinedGroupByCondition(Integer appId, String userId, int excludeRole);
}
