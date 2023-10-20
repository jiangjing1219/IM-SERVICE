package com.jiangjing.im.service.friendship.model.req;

import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;


/**
 * @author Admin
 */
@Data
public class ApproverFriendRequestReq extends RequestBase {

    private Long id;

    //1同意 2拒绝
    private Integer status;
}
