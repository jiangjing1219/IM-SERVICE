package com.jiangjing.im.service.friendship.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Admin
 */
@Data
@TableName("im_friendship_group")
public class ImFriendShipGroupEntity {

    /**
     * 分组主键
     */
    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;

    /**
     * 目标用户
     */
    private String fromId;

    /**
     * appid
     */
    private Integer appId;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 序列号
     **/
    private Long sequence;

    /**
     * 删除状态
     */
    private int delFlag;


}
