package com.seblong.okr.utils;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.RedisTemplate;

public class CoinLockUtil {

	private final static ConcurrentHashMap<String, CoinLock> COIN_LOCKS = new ConcurrentHashMap<String, CoinLock>();
	
	public  static CoinLock get(RedisTemplate<String, Object> redisTemplate, String mainUser) {
		CoinLock coinLock = COIN_LOCKS.get(mainUser);
		if( coinLock == null ) {
			coinLock = new CoinLock(redisTemplate, mainUser);
			COIN_LOCKS.put(mainUser, coinLock);
		}
		return coinLock;
	}
	
}
