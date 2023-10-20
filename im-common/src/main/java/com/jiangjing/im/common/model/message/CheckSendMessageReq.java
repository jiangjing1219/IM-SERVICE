package com.jiangjing.im.common.model.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
public class CheckSendMessageReq implements Serializable {

    private String fromId;

    private String toId;

    private String groupId;

    private Integer appId;

    private Integer command;

}
