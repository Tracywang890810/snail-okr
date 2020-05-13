package com.seblong.okr.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.seblong.okr.entities.OKR;

@Repository
public interface OKRRepository extends MongoRepository<OKR, ObjectId>{

	OKR findByUserAndPeriod(String user, String period);
	
	long deleteByEnterpriseId(String companyId);
}
