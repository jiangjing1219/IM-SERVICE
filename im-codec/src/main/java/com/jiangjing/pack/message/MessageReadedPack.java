package com.jiangjing.pack.message;

import lombok.Data;

/**
 * 已读
 *
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
public class MessageReadedPack {

    private long messageSequence;

    private String fromId;

    private String groupId;

    private String toId;

    private Integer conversationType;
}
