package com.project.mything.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean saveData(String key, String value, Long expire) {
            redisTemplate.opsForValue().set(key, value, expire, TimeUnit.MILLISECONDS);
            return true;
    }

    @Override
    public Optional<String> getData(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    @Override
    public boolean deleteData(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @Override
    public Optional<String> getDataAndDelete(String key) {
        String deletedData = redisTemplate.opsForValue().getAndDelete(key);
        return Optional.ofNullable(deletedData);
    }
}
