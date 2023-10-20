package com.jiangjing.im.service.message.model.resp;

import lombok.Data;

/**
 * 群聊历史消息
 *
 * @author
 */
@Data
public class GroupMessageHistoryResp {

    private String messageKey;

    private String fromId;

    private String groupId;

    private int messageRandom;

    private long messageTime;

    private String messageBody;
    /**
     * 这个字段缺省或者为 0 表示需要计数，为 1 表示本条消息不需要计数，即右上角图标数字不增加
     */
    private int badgeMode;

    private Long messageLifeTime;

    private Integer appId;

    /**
     * 序列号
     */
    private Long messageSequence;

    private Integer delFlag;

}
