package com.example.imtcp.register;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Admin
 */
@Configuration
public class CuratorConfig {

    @Value("${curator.connect-string}")
    private String connectString;

    @Value("${curator.session-timeout-ms}")
    private int sessionTimeoutMs;

    @Value("${curator.connection-timeout-ms}")
    private int connectionTimeoutMs;

    @Bean(initMethod = "start", destroyMethod = "close")
    public CuratorFramework curatorFramework() {
        return CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connectionTimeoutMs)
                .retryPolicy(new ExponentialBackoffRetry(5, 1000)) // 重试5次，每次间隔1秒
                .build();
    }
}
