package com.seblong.okr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.seblong.okr.properties.AbstractMongoProperties;

@Configuration
@ConfigurationProperties("snail.okr.mongodb")
@EnableMongoRepositories(basePackages = {"com.seblong.okr.repositories"}, mongoTemplateRef =  "mongoTemplate")
public class MongoConfig extends AbstractMongoProperties{

	@Override
	@Bean(name = "mongoTemplate")
	public MongoTemplate getMongoTemplate() {
		return new MongoTemplate(mongoDbFactory());
	}

}
