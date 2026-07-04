package com.example.springboot.rediscache.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RedisErrorScheduler {

    private static final Logger log = LoggerFactory.getLogger(RedisErrorScheduler.class);

    private final RedisErrorAccumulator redisErrorAccumulator;

    public RedisErrorScheduler(RedisErrorAccumulator redisErrorAccumulator) {
        this.redisErrorAccumulator = redisErrorAccumulator;
    }

    // Add a default value so the scheduler still runs even if the property isn't loaded
    @Scheduled(fixedDelayString = "${app.redis.reset-interval:10000}")
    public void resetErrorCount() {
        log.debug("RedisErrorScheduler.resetErrorCount invoked - current errorCount={}", redisErrorAccumulator.getCount());
        redisErrorAccumulator.reset();
        log.info("Redis error counter reset to 0");
    }
}
