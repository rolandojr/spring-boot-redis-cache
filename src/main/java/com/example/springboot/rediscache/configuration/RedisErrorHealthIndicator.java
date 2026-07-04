package com.example.springboot.rediscache.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("redis_audit")
public class RedisErrorHealthIndicator implements ReactiveHealthIndicator {

    private final RedisErrorAccumulator errorAccumulator;
    private final int threshold;

    public RedisErrorHealthIndicator(RedisErrorAccumulator errorAccumulator,
                                     @Value("${app.redis.error-threshold}") int threshold) {
        this.errorAccumulator = errorAccumulator;
        this.threshold = threshold;
    }

    @Override
    public Mono<Health> health() {
        int count = errorAccumulator.getCount();
        if (errorAccumulator.isThresholdReached(threshold)) {
            return Mono.just(Health.down()
                    .withDetail("errorCount", count)
                    .withDetail("threshold", threshold)
                    .build());
        }
        return Mono.just(Health.up()
                .withDetail("errorCount", count)
                .withDetail("threshold", threshold)
                .build());
    }
}
