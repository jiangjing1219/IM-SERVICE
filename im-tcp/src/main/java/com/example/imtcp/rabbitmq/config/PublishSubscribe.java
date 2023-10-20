package com.example.imtcp.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author admin
 * <p>
 * PublishSubscribe 模式，需要指定交换机，但是不需要 Routing key ，因为 Fanout Exchange 给绑定队列的广播消息都是一样的，多个服务端监听同一个队列，所有服务端接收到的消息都是一致的
 */
@Configuration
public class PublishSubscribe {

    @Bean
    public Exchange fanoutExchange() {
        return ExchangeBuilder.fanoutExchange("user_login_broadcast").durable(true).build();
    }

    @Bean
    public Queue fanoutQueue() {
        return QueueBuilder.durable("user_login_queue").build();
    }


    @Bean
    public Binding fanoutBinging() {
        return BindingBuilder.bind(fanoutQueue()).to(fanoutExchange()).with("").noargs();
    }

}
