package com.jiangjing.im.common.model.message;

import com.jiangjing.im.common.model.ClientInfo;
import lombok.Data;
import lombok.ToString;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
@ToString
public class MessageContent extends ClientInfo {

    private String messageId;

    private String fromId;

    private String toId;

    private String messageBody;

    private Long messageTime;

    private String extra;

    private Long messageKey;

    private long messageSequence;

}
