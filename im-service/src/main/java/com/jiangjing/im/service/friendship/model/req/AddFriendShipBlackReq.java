package com.jiangjing.im.service.friendship.model.req;

import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * @author Admin
 */
@Data
public class AddFriendShipBlackReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String fromId;

    private String toId;
}
