package com.seblong.okr.properties;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.clients.jedis.Jedis;

public abstract class AbstractRedisProperties {

	private String host;
	private int port;
	private String password;
	private int maxTotal;
	private int maxIdle;
	private boolean testOnIdle;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public boolean isTestOnIdle() {
		return testOnIdle;
	}

	public void setTestOnIdle(boolean testOnIdle) {
		this.testOnIdle = testOnIdle;
	}

	public RedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
		standaloneConfiguration.setHostName(host);
		standaloneConfiguration.setPort(port);
		standaloneConfiguration.setPassword(RedisPassword.of(password));
		GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
		poolConfig.setMaxTotal(maxTotal);
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setTestWhileIdle(testOnIdle);
		JedisClientConfiguration clientConfiguration = JedisClientConfiguration.builder().usePooling()
				.poolConfig(poolConfig).build();
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(standaloneConfiguration,
				clientConfiguration);
		return jedisConnectionFactory;
	}

}
