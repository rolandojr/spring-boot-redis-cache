package com.example.springboot.rediscache.configuration;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RedisErrorAccumulator {

    private final AtomicInteger errorCount = new AtomicInteger(0);

    public void increment(){
        errorCount.incrementAndGet();
    }

    public void reset(){
        errorCount.set(0);
    }

    public int getCount(){
        return errorCount.get();
    }

    public boolean isThresholdReached(int threshold){
        return errorCount.get() > threshold;
    }

}
