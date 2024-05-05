package com.example.imtcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jingjing
 * @date 2023/6/23 17:01
 */
@Data
@Component
@ConfigurationProperties(prefix = "im")
public class ImConfigInfo {

    /**
     * netty 启动端口
     */
    private Integer tcpPort;

    /**
     * websocket 启动端口  重构 reactor
     */
    private Integer webSocketPort;

    /**
     * boss group 的工作线程数
     */
    private Integer bossThreadSize;

    /**
     * netty 负责读写的线程数
     */
    private Integer workThreadSize;

    /**
     * 心跳超时时间
     */
    private Long heartBeatTime;

    /**
     * 心跳超时次数
     */
    private Long maxReadTimeoutCount;

    /**
     * 当前服务的标识
     */
    private Integer brokerId;

    /**
     * 登录模式（登录的设备：web、windows、mac、ios、Android）
     * 1、单端登录：只允许一端在线
     * 2、双端登录（移动端和电脑端）
     * 3、三端登录（web、电脑端、移动端）
     * 4、多端登录（不做登录限制）
     */
    private Integer loginModel;

    /**
     * zk连接地址
     */
    private String zkAddr;

    /**
     * zk连接超时时间
     */
    private Integer zkConnectTimeOut;


}
