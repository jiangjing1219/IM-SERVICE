package com.jiangjing.im.service.user.model.req;

import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
public class GetUserSequenceReq extends RequestBase {

    private String userId;

}
