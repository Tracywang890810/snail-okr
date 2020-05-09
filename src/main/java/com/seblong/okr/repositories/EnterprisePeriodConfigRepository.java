package com.seblong.okr.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.seblong.okr.entities.EnterprisePeriodConfig;

@Repository
public interface EnterprisePeriodConfigRepository extends MongoRepository<EnterprisePeriodConfig, ObjectId>{

	EnterprisePeriodConfig findByEnterpriseId( String enterpriseId );
	
}
