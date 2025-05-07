package com.example.screen_golf.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

	private final RedisTemplate<String, String> redisTemplate;

	public RedisUtil(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void setData(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public void setDataExpire(String key, String value, long duration) {
		redisTemplate.opsForValue().set(key, value, duration, TimeUnit.MILLISECONDS);
	}

	public String getData(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void deleteData(String key) {
		redisTemplate.delete(key);
	}

	public boolean hasKey(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}
} 