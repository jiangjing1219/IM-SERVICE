package com.jiangjing.im.service.user.model.req;

import com.jiangjing.im.common.model.RequestBase;
import lombok.Data;

import java.util.List;


/**
 * @author Admin
 */
@Data
public class GetUserInfoReq extends RequestBase {

    private List<String> userIds;


}
