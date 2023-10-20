package com.jiangjing.im.service.user.model.req;

import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
public class SubscribeUserOnlineStatusReq extends RequestBase {

    private List<String> subUserIds;

    private Long subTime;


}
