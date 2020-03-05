package com.seblong.okr.repositories;

import com.seblong.okr.entities.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, ObjectId> {

    List<Comment> findByPeriodAndEmployeeAndStatus(String period, String employeeId, String status, Sort sort);

    List<Comment> findByPeriodAndOwnerAndStatus(String period, String employeeId, String status, Sort sort);
}
