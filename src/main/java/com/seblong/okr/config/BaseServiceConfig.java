package com.seblong.okr.config;

import com.seblong.okr.utils.SnowflakeIdWorker;


public abstract class BaseServiceConfig {

	protected static SnowflakeIdWorker snowflakeId;
	
	public static SnowflakeIdWorker getSnowflakeId() {
		return snowflakeId;
	}
	
}
