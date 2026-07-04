package com.example.springboot.rediscache.configuration;

import org.redisson.api.RedissonReactiveClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("redis_validation")
public class RedisHealthIndicator implements ReactiveHealthIndicator {

    private final ReactiveStringRedisTemplate redisTemplate;

    public RedisHealthIndicator(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Health> health() {
        return redisTemplate
                .execute(ReactiveRedisConnection::ping)
                .next()
                .map(response -> Health.up()
                        .withDetail("ping", response)
                        .build())
                .onErrorResume(ex -> Mono.just(
                        Health.down()
                                .withDetail("error", ex.getMessage())
                                .build()));
    }
}
