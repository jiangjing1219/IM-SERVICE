package com.jiangjing.im.common.model.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author Admin
 */
@Data
@ToString
public class MessageBody {

    /**
     * 消息类型 1 JSON、2 XML
     */
    int type;

    /**
     * 消息内容
     */
    String content;


    public MessageBody(String content) {
        this.type = 1;
        this.content = content;
    }

}
