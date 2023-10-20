package com.jiangjing.proto;

import lombok.Data;

/**
 * @author jingjing
 * @date 2023/6/23 17:55
 */
@Data
public class Message {

    private MessageHeader messageHeader;

    private Object messagePackage;

    @Override
    public String toString() {
        return "Message{" +
                "massageHeader=" + messageHeader +
                ", messagePackage=" + messagePackage +
                '}';
    }
}
