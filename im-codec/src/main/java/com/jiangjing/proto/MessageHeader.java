package com.jiangjing.proto;

import lombok.Data;
import lombok.ToString;

/**
 * 自定义请求头
 *
 * @author jingjing
 * @date 2023/6/23 17:48
 */

@Data
@ToString
public class MessageHeader {

    /**
     * 指令类型
     */
    private Integer command;

    /**
     * 自定义协议的版本号
     */
    private Integer version;

    /**
     * 客户端类型
     */
    private Integer clientType;

    /**
     * appid
     */
    private Integer appId;

    /**
     * 消息类型（支持 0x0:Json,0x1:ProtoBuf,0x2:Xml,默认:0x0）
     */
    private Integer messageType = 0x0;

    /**
     * 登陆设备标识的长度
     */
    private Integer imeiLength;

    /**
     * 消息体长度
     */
    private Integer length;

    /**
     * 登陆设备
     */
    private String imei;
}
