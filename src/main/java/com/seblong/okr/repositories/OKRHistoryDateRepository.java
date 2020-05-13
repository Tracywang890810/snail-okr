package com.seblong.okr.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.seblong.okr.entities.OKRHistoryDate;

@Repository
public interface OKRHistoryDateRepository extends MongoRepository<OKRHistoryDate, ObjectId>{

	OKRHistoryDate findByUserAndPeriod(String user, String period);

	long deleteByEnterpriseId(String companyId);
}
