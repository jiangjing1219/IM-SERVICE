package com.jiangjing.pack.user;

import lombok.Data;

/**
 * 登录完成之后回复客户端 ack 包
 *
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Data
public class LoginAckPack {

    private String userId;

}
