package com.seblong.okr.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import com.seblong.okr.entities.OKR.Objective;

public class ObjectiveCache extends LRUCache<Objective> {

	private static final long serialVersionUID = -1152851364675408146L;

	public ObjectiveCache(int size) {
		this(size, null);
	}

	public ObjectiveCache(int size, RedisTemplate<String, Object> redisTemplate) {
		super(size, redisTemplate);
	}

	public Objective get(String id) {
		if( !StringUtils.isEmpty(id) ){
			return super.get(id);
		}
		return null;
	}

	public Objective remove(Objective objective) {
		if( objective != null ) {
			return super.remove(objective.getId().toString());
		}
		return null;
	}

	public Objective put(Objective objective) {
		if( objective != null ) {
			return super.put(objective.getId().toString(), objective);
		}
		return null;
	}

}
