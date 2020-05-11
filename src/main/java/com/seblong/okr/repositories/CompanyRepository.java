package com.seblong.okr.repositories;

import com.seblong.okr.entities.Company;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends MongoRepository<Company, ObjectId> {

    Company findByCorpId(String corpId);
}
