package com.seblong.okr.utils;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

public class LRUCache<V> extends LinkedHashMap<String, V> {

	private static final long serialVersionUID = 4028088213386879567L;

	protected final String KEY_PREFIX;

	private RedisTemplate<String, Object> redisTemplate;
	
	private int size = 100;
	
	private int seconds = 3600;

	public LRUCache() {
		this.KEY_PREFIX = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]
				.getTypeName();
	}

	public LRUCache(int size) {
		this();
		this.size = size;
	}

	public LRUCache(int size, int seconds) {
		this();
		this.size = size;
		this.seconds = seconds;
	}

	public LRUCache(int size, RedisTemplate<String, Object> redisTemplate) {
		this();
		this.size = size;
		this.redisTemplate = redisTemplate;
	}
	
	public LRUCache(RedisTemplate<String, Object> redisTemplate) {
		this();
		this.redisTemplate = redisTemplate;
	}

	public LRUCache(int size, int seconds,RedisTemplate<String, Object> redisTemplate) {
		this();
		this.redisTemplate = redisTemplate;
		this.size = size;
		this.seconds = seconds;
	}
	

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<String, V> eldest) {
		return size() > size;
	}

	@SuppressWarnings("unchecked")
	protected V get(String key) {
		if (!StringUtils.isEmpty(key)) {
			key = generateKey(key);
			V o = super.get(key);
			if (o == null && this.redisTemplate != null) {
				Object result = redisTemplate.boundValueOps(key).get();
				if (result != null) {
					o = (V) result;
					put(key, o);
				}
			}
			return o;
		}
		return null;
	}

	protected V remove(String key) {
		if (!StringUtils.isEmpty(key)) {
			key = generateKey(key);
			V v = super.remove(key);
			if (this.redisTemplate != null) {
				redisTemplate.delete(key);
			}
			return v;
		}
		return null;
	}

	@Override
	public V put(String key, V value) {
		key = generateKey(key);
		V v = super.put(key, value);
		if (this.redisTemplate != null)
			redisTemplate.boundValueOps(key).set(value, seconds, TimeUnit.SECONDS);
		return v;
	}

	private String generateKey(String key) {
		StringBuilder sb = new StringBuilder(KEY_PREFIX);
		sb.append("::").append(key);
		return sb.toString();
	}
}
