package com.seblong.okr.repositories;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.seblong.okr.entities.OKRPeriod;
import com.seblong.okr.entities.OKRPeriod.Type;

@Repository
public interface OKRPeriodRepository extends MongoRepository<OKRPeriod, ObjectId>{

	List<OKRPeriod> findByEnterpriseIdAndYear( String enterpriseId, String year );
	
	long countByEnterpriseIdAndTypeAndStartDate( String enterpriseId, Type type, String startDate );
}
