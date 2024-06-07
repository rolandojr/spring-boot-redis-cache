package com.example.springboot.rediscache.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonProperties {

    private String host;
    private int port;
    private String password;
    private boolean ssl;
    private int timeout;
    private int connectTimeout;
    private String clientName;
}
