package com.seblong.okr.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.seblong.okr.properties.AbstractRedisProperties;

@ConfigurationProperties(prefix = "snail.okr.redis")
@Configuration
public class RedisConfig extends AbstractRedisProperties{

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		return redisTemplate;
	}
	
	@Bean
	public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {
		RedisCacheManager redisCacheManager = RedisCacheManager
				.builder(redisTemplate.getConnectionFactory())
				.cacheDefaults(
						RedisCacheConfiguration
								.defaultCacheConfig()
								.entryTtl(Duration.ofSeconds(86400))
								.disableCachingNullValues()
								.serializeKeysWith(
										RedisSerializationContext.SerializationPair
												.fromSerializer(new StringRedisSerializer()))
								.serializeValuesWith(
										RedisSerializationContext.SerializationPair
												.fromSerializer(new GenericJackson2JsonRedisSerializer()))).build();
		return redisCacheManager;
	}
	
}
