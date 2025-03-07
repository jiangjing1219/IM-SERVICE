package com.jiangjing.pack.message;

import lombok.Data;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
public class ChatMessageAck {

    private String messageId;
    private Long messageSequence;
    private Long messageKey;
    private String toId;

    public ChatMessageAck(String messageId) {
        this.messageId = messageId;
    }

    public ChatMessageAck(String messageId, Long messageSequence, Long messageKey, String toId) {
        this.messageId = messageId;
        this.messageSequence = messageSequence;
        this.messageKey = messageKey;
        this.toId = toId;
    }

    public ChatMessageAck(String messageId, Long messageSequence) {
        this.messageId = messageId;
        this.messageSequence = messageSequence;
    }

}
