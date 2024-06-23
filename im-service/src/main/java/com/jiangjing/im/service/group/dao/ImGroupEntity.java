package com.jiangjing.im.service.group.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiangjing.im.service.group.model.req.GroupMemberDto;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
@TableName("im_group")
public class ImGroupEntity {

    @TableId(value = "group_id")
    private String groupId;

    private Integer appId;

    //群主id
    private String ownerId;

    //群类型 1私有群（类似微信） 2公开群(类似qq）
    private Integer groupType;

    private String groupName;

    // 是否全员禁言，0 不禁言；1 全员禁言。
    private Integer mute;

//    申请加群选项包括如下几种：
//    0 表示禁止任何人申请加入
//    1 表示需要群主或管理员审批
//    2 表示允许无需审批自由加入群组
    private Integer applyJoinType;

    //群简介
    private String introduction;

    //群公告
    private String notification;

    //群头像
    private String photo;

    //群成员上限
    private Integer maxMemberCount;

    //群状态 0正常 1解散
    private Integer status;

    private Long sequence;

    private Long createTime;

    private Long updateTime;

    private String extra;

    @TableField(exist = false)
    private List<GroupMemberDto> memberList;
}
