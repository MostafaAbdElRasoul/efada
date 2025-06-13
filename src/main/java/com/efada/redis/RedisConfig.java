/**
 * 
 */
package com.efada.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
@EnableRedisRepositories(basePackages = "com.efada.redis.repository")
public class RedisConfig {

	@Value("${redis.host}")
	private String redisHost;
	@Value("${redis.port}")
	private Integer redisPort;
   
	@Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
	}

    @Bean
    public RedisTemplate<String, Object> userResourceRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return template;
    }

    @Bean
    public HashOperations hashOperations(RedisTemplate<String, Object> userResourceRedisTemplate) {
        return userResourceRedisTemplate.opsForHash();
    }

}
