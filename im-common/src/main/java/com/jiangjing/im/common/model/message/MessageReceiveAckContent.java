package com.jiangjing.im.common.model.message;

import com.jiangjing.im.common.model.ClientInfo;
import lombok.Data;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
public class MessageReceiveAckContent extends ClientInfo {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;


}
