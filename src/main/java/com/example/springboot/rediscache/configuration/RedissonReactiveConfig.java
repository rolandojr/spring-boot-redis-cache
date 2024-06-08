package com.example.springboot.rediscache.configuration;

import com.example.springboot.rediscache.models.User;
import org.redisson.Redisson;
import org.redisson.api.RListReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.springboot.rediscache.utils.Constants.USER_CACHE;
import static com.example.springboot.rediscache.utils.Constants.USER_LIST_CACHE;

@Configuration
public class RedissonReactiveConfig {

    private static final String REDIS_PROTOCOL_PREFIX = "redis://";
    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

    @Bean
    public RedissonReactiveClient redissonReactiveClient(RedissonProperties redissonProperties) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(getAddress(redissonProperties))
                .setPassword(redissonProperties.getPassword())
                .setClientName(redissonProperties.getClientName())
                .setTimeout(redissonProperties.getTimeout())
                .setConnectionPoolSize(50)
                .setConnectionMinimumIdleSize(10)
                .setConnectTimeout(redissonProperties.getConnectTimeout());
        return Redisson.create(config).reactive();
    }

    private String getAddress(RedissonProperties redissonProperties) {
        return getRedisProtocolPrefix(redissonProperties) + redissonProperties.getHost() + ":" + redissonProperties.getPort();
    }

    private String getRedisProtocolPrefix(RedissonProperties redissonProperties) {
        return redissonProperties.isSsl() ? REDISS_PROTOCOL_PREFIX : REDIS_PROTOCOL_PREFIX;
    }

    @Bean
    public RMapReactive<Long, User> redissonRMapReactiveClient(RedissonReactiveClient redissonReactiveClient) {
        return redissonReactiveClient.getMap(USER_CACHE);
    }

    @Bean
    public RListReactive<User> redissonRListReactiveClient(RedissonReactiveClient redissonReactiveClient) {
        return redissonReactiveClient.getList(USER_LIST_CACHE);
    }

}
