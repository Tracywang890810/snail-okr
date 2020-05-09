package com.seblong.okr.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import com.seblong.okr.entities.OKR;

public class OKRCache extends LRUCache<OKR> {

	private static final long serialVersionUID = 8567370153214672786L;

	public OKRCache(int size) {
		this(size, null);
	}

	public OKRCache(int size, RedisTemplate<String, Object> redisTemplate) {
		super(size, redisTemplate);
	}

	public OKR get(String user, String period) {
		if( !StringUtils.isEmpty(user) && !StringUtils.isEmpty(period) ) {
			String key = user + "::" + period;
			return super.get(key);
		}
		return null;
	}

	public OKR remove(OKR okr) {
		if( okr != null ) {
			String key = okr.getUser() + "::" + okr.getPeriod();
			return super.remove(key);
		}
		return null;
	}

	public OKR put(OKR okr) {
		if( okr != null ) {
			String key = okr.getUser() + "::" + okr.getPeriod();
			return super.put(key, okr);
		}
		return null;
	}

}
