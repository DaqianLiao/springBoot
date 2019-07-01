package com.ldq.zkdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "zk")
@PropertySource("classpath:zk.properties")
public class ZKConfig {
    /**
     * zk.enabled=true
     * zk.server=localhost:2181
     * zk.sessionTimeout=5000
     * zk.connectionTimeoutMs=5000
     * zk.maxRetries=3
     * zk.baseSleepTimeMs=1000
     */

    private boolean enabled;
    private String server;
    private int sessionTimeoutMs;
    private int connectionTimeoutMs;
    private int maxElapsedTimeMs;
    private int baseSleepTimeMs;
    private int maxRetries;

}