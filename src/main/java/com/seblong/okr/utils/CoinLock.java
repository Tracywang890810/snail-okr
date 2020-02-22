package com.seblong.okr.utils;

import org.springframework.data.redis.core.RedisTemplate;

public class CoinLock {

	private RedisLock redisLock;
	
	public CoinLock(RedisTemplate<String, Object> redisTemplate, String mainUser) {
		this.redisLock = new RedisLock(redisTemplate, "COIN::OPERATION::" + mainUser, 30000, 20000 );
	}

	public RedisLock getRedisLock() {
		return redisLock;
	}

	public void setRedisLock(RedisLock redisLock) {
		this.redisLock = redisLock;
	}

	public boolean lock() throws InterruptedException {
		return this.redisLock.lock();
	}
	
	public void unlock() {
		this.redisLock.unlock();
	}
	
	public boolean testLock() {
		return this.redisLock.testLock();
	}
}
