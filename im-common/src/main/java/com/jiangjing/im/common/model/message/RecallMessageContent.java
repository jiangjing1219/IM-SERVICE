package com.jiangjing.im.common.model.message;

import com.jiangjing.im.common.model.ClientInfo;
import lombok.Data;

/**
 * @author: Chackylee
 * @description:
 **/
@Data
public class RecallMessageContent extends ClientInfo {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageTime;

    private Long messageSequence;

    // 会话类型，单聊/群聊
    private Integer conversationType;


//    {
//        "messageKey":419455774914383872,
//            "fromId":"lld",
//            "toId":"lld4",
//            "messageTime":"1665026849851",
//            "messageSequence":2,
//            "appId": 10000,
//            "clientType": 1,
//            "imei": "web",
//    "conversationType":0
//    }
}
