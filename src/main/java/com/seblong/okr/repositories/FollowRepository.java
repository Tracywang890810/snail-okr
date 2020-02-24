package com.seblong.okr.repositories;

import com.seblong.okr.entities.Follow;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends MongoRepository<Follow, ObjectId> {

    List<Follow> findByEmployee(String employeeId);

    Follow findByEmployeeAndTarget(String toString, String toString1);
}
