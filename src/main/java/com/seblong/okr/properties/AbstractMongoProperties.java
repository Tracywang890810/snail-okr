package com.seblong.okr.properties;

import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClientURI;

public abstract class AbstractMongoProperties {

	private String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public MongoDbFactory mongoDbFactory() {
		MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(new MongoClientURI(url));
		return mongoDbFactory;
	}
	
	abstract public MongoTemplate getMongoTemplate();
}
