package com.efada.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisHandler {

	@Autowired
	private RedisTemplate<Object,Object> redisTemplate;
	
	public void set(String key , Object obj) {
		redisTemplate.opsForValue().set(key, obj);
	}
	
	public void setWithExpiryInMin(Long key , Object obj , Long timeout) {
		redisTemplate.opsForValue().set(key, obj);
		redisTemplate.expire(key, timeout, TimeUnit.MINUTES);
	}
	
	public void setWithExpiryInMin(String key , Object obj , Long timeout) {
		redisTemplate.opsForValue().set(key, obj);
		redisTemplate.expire(key, timeout, TimeUnit.MINUTES);
	}
	
	public void setWithExpiry(String key , Object obj , Long timeout , TimeUnit unit) {
		redisTemplate.opsForValue().set(key, obj);
		redisTemplate.expire(key, timeout, unit);
	}
	
	public Object get(String key) {
		Object obj = redisTemplate.opsForValue().get(key);
		return obj;
	}
	
	public void delete(String key) {
		redisTemplate.delete(key);
	}
}
