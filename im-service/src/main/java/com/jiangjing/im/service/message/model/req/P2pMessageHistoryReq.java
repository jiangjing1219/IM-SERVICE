package com.jiangjing.im.service.message.model.req;

import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 单聊消息查询
 *
 * @author
 */
@Data
public class P2pMessageHistoryReq extends RequestBase {

    @NotEmpty(message = "ownerId 不能为空")
    private String ownerId;

    @NotEmpty(message = "toId 不能为空")
    private String toId;

    private String messageSequence;

    /**
     * 是否是大于该 messageSequence
     */
    private boolean isGt;

    private long startTime;

    private long endTime;

}
