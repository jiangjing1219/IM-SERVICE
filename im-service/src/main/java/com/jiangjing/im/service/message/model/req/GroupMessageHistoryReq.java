package com.jiangjing.im.service.message.model.req;

import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 群聊消息查询，采用的读扩散的方式，只有一个群聊消息副本，所以查询的时候不关心 form_id
 *
 * @author
 */
@Data
public class GroupMessageHistoryReq extends RequestBase {

    @NotEmpty(message = "groupId 不能为空")
    private String groupId;

    private String messageSequence;

    /**
     * 是否是大于该 messageSequence
     */
    private boolean isGt;

    private long startTime;

    private long endTime;
}
