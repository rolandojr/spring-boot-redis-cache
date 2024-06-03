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

    @Bean
    public RedissonReactiveClient redissonReactiveClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379");
        return Redisson.create(config).reactive();
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
