package com.jiangjing.im.service.user.model;

import com.jiangjing.im.common.model.ClientInfo;
import lombok.Data;

/**
 * @description: status区分是上线还是下线
 * @author: lld
 * @version: 1.0
 */
@Data
public class UserStatusChangeNotifyContent extends ClientInfo {


    private String userId;

    //服务端状态 1上线 2离线
    private Integer status;



}
