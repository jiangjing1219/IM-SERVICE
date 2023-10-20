package com.jiangjing.im.service.group.model.req;

import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;

/**
 * @author: Chackylee
 * @description:
 **/
@Data
public class GetGroupReq extends RequestBase {

    private String groupId;

}
