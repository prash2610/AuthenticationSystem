package com.authsystem.service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redisTemplate;

    public void saveRefreshToken(String key, String token) {

        System.out.println("Saving refreshtoken");
        redisTemplate.opsForValue().set("refresh:" + key, token, 7, TimeUnit.DAYS);
    }

    public String getRefreshToken(String key) {
        return redisTemplate.opsForValue().get( "refresh:"+key);
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete("refresh:" + email);
    }

    public void deleteAllUserTokens(String email) {
        Set<String> keys = redisTemplate.keys("refresh:" + email + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
