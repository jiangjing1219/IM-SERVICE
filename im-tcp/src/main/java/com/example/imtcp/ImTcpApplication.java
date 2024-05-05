package com.example.imtcp;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Admin
 */
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class ImTcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImTcpApplication.class, args);
    }

}
