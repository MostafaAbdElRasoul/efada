package com.efada.security.ratelimiter;

import com.efada.redis.RedisHandler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private final RedisHandler redisHandler;

    @Value("${rate-limiter.max-requests}")
    private int maxRequests;

    @Value("${rate-limiter.time-window-sec}")
    private long timeWindowSec;

    public RateLimiterService(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public boolean isAllowed(String key) {
        Object val = redisHandler.get(key);
        if (val == null) {
            // First request: set value to 1 with expiry
            redisHandler.setWithExpiry(key, 1, timeWindowSec, TimeUnit.SECONDS);
            return true;
        }

        int count = Integer.parseInt(val.toString());
        if (count >= maxRequests) {
            return false;
        }

        // Increment the counter
        redisHandler.setWithExpiry(key, count + 1, timeWindowSec, TimeUnit.SECONDS);
        return true;
    }
}
