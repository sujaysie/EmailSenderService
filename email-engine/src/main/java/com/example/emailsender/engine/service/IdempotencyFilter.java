package com.example.emailsender.engine.service;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class IdempotencyFilter {
    private static final Duration TTL = Duration.ofDays(7);
    private final StringRedisTemplate redisTemplate;

    public IdempotencyFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isDuplicate(String eventId) {
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent("idem:" + eventId, "1", TTL);
        return Boolean.FALSE.equals(isNew);
    }
}
