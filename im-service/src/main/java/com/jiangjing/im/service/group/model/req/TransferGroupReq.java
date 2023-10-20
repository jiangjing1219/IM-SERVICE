package com.jiangjing.im.service.group.model.req;

import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * @author Admin
 */
@Data
public class TransferGroupReq extends RequestBase {

    @NotNull(message = "群id不能为空")
    private String groupId;

    private String ownerId;

}
