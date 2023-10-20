package com.jiangjing.im.service.user.model.req;

import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
public class SetUserCustomerStatusReq extends RequestBase {

    private String userId;

    private String customText;

    private Integer customStatus;

}
