package com.seblong.okr.repositories;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.seblong.okr.entities.OKRPeriod;

@Repository
public interface OKRPeriodRepository extends MongoRepository<OKRPeriod, ObjectId>{

	List<OKRPeriod> findByYear( int year );
	
	long countByYear( int year );
}
