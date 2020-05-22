package com.seblong.okr.repositories;

import com.seblong.okr.entities.Aligning;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AligningRepository extends MongoRepository<Aligning, ObjectId> {

    Aligning findByObjective(String objectiveId);

    List<Aligning> findByTargetO(String objectiveId);

    List<Aligning> findByEmployee(String employee);

    void deleteByCompanyId(String companyId);
}
