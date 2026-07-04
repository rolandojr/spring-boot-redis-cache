package com.example.springboot.rediscache.configuration;

import org.redisson.api.RedissonReactiveClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RedisDisconnectScheduler {

    private static final Logger log = LoggerFactory.getLogger(RedisDisconnectScheduler.class);
    private final RedissonReactiveClient redissonReactiveClient;
    private final AtomicBoolean disconnected = new AtomicBoolean(false);

    public RedisDisconnectScheduler(RedissonReactiveClient redissonReactiveClient) {
        this.redissonReactiveClient = redissonReactiveClient;
    }

    @Scheduled(initialDelayString = "${app.redis.disconnect-initial-delay:30000}",
               fixedDelayString = "${app.redis.disconnect-interval:60000}")
    public void simulateRedisDisconnect() {
        if (disconnected.compareAndSet(false, true)) {
            log.warn("Simulating Redis disconnection: shutting down the Redisson client.");
            try {
                redissonReactiveClient.shutdown();
            } catch (Exception ex) {
                log.error("Failed to shut down Redis client during simulated disconnect", ex);
            }
        } else {
            log.debug("Redis disconnect scheduler executed, but the client is already shut down.");
        }
    }
}
