package com.seblong.okr.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.seblong.okr.entities.OKRHistory;

@Repository
public interface OKRHistoryRepository extends MongoRepository<OKRHistory, ObjectId>{

	OKRHistory findByUserAndPeriodAndDate(String user, String period, String date);

	long deleteByEnterpriseId(String companyId);
}
