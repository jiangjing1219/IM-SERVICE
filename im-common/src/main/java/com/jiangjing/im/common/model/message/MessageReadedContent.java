package com.jiangjing.im.common.model.message;

import com.jiangjing.im.common.model.ClientInfo;
import lombok.Data;

/**
 * 不需要 messageKey 只要小于该  messageSequence 的消息都是已读
 *
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
public class MessageReadedContent extends ClientInfo {

    private long messageSequence;

    private String fromId;

    private String groupId;

    private String toId;

    private Integer conversationType;
}
