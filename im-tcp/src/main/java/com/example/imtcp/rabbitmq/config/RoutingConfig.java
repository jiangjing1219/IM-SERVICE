package com.example.imtcp.rabbitmq.config;


import com.example.imtcp.config.ImConfigInfo;
import com.jiangjing.im.common.constant.Constants;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author admin
 *
 * Direct 模式 Exchange 是根据 Routing key 的精确匹配的将消息推送到相应的 Queue 里面，Counsumer 监听队列获取相应的消息
 */
@Configuration
public class RoutingConfig {
    @Autowired
    private ImConfigInfo imConfigInfo;

    /**
     * 声明一个全匹配 routing key 的交换机，名称为： pipeline2MessageService
     * @return
     */
    @Bean
    public Exchange directExchange() {
        return ExchangeBuilder.directExchange(Constants.RabbitConstants.MESSAGE_SERVICE_2_IM).durable(true).build();
    }

    /**
     * 声明队列，名称为：pipeline2MessageService_brokerId
     * @return
     */
    @Bean
    public Queue directQueue() {
        return QueueBuilder.durable(Constants.RabbitConstants.MESSAGE_SERVICE_2_IM +"_"+imConfigInfo.getBrokerId()).build();
    }

    /**
     * 将队列绑定到交换机上，并执行分发的 routing key ： pipeline2MessageService_brokerId  ，只接收当前服务自己的消息
     * @return
     */
    @Bean
    public Binding directBinging() {
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(String.valueOf(imConfigInfo.getBrokerId())).noargs();
    }
}
