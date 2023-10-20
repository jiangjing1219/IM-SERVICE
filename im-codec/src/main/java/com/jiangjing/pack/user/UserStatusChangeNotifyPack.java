package com.jiangjing.pack.user;

import com.jiangjing.im.common.model.UserSession;
import lombok.Data;

import java.util.List;

/**
 *
 *
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
public class UserStatusChangeNotifyPack {

    private Integer appId;

    private String userId;

    private Integer status;

    private List<UserSession> client;

}
