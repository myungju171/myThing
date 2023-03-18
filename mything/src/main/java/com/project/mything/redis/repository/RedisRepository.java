package com.project.mything.redis.repository;

import java.util.Optional;

public interface RedisRepository {
    boolean saveData(String key, String value, Long expire);
    Optional<String> getData(String key);
    boolean deleteData(String key);
    Optional<String> getDataAndDelete(String key);
}
