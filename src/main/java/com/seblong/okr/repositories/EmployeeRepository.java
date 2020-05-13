package com.seblong.okr.repositories;

import com.seblong.okr.entities.Employee;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, ObjectId> {

    @Query("{ 'name' : { $regex : ?0 } }")
    List<Employee> queryByName(String keyword);

    Employee findByUserId(String userId);

    Employee findByUserIdAndCorpId(String userId, String corpId);
}
