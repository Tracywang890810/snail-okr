package com.seblong.okr.services.impl;

import com.seblong.okr.entities.Comment;
import com.seblong.okr.enums.EntityStatus;
import com.seblong.okr.repositories.CommentRepository;
import com.seblong.okr.services.CommentService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepo;

    @Override
    public Comment comment(String employeeId, String period, String ownerId, String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setEmployee(employeeId);
        comment.setOwner(ownerId);
        comment.setPeriod(period);
        comment = commentRepo.save(comment);
        return comment;
    }

    @Override
    public void remove(String unique) {
        commentRepo.deleteById(new ObjectId(unique));
    }

    @Override
    public void solve(String unique) {
        Optional<Comment> optional = commentRepo.findById(new ObjectId(unique));
        if(optional != null){
            Comment comment = optional.get();
            comment.setStatus(EntityStatus.SOLVE.toString());
            comment.setUpdated(System.currentTimeMillis());
            commentRepo.save(comment);
        }
    }

    @Override
    public List<Comment> list(String employeeId, String period, String status) {
        Sort sort = new Sort(Sort.Direction.DESC);
        return commentRepo.findByPeriodAndEmployeeAndStatus(period, employeeId, status, sort);
    }

    @Override
    public void reopen(String unique) {
        Optional<Comment> optional = commentRepo.findById(new ObjectId(unique));
        if(optional != null){
            Comment comment = optional.get();
            comment.setStatus(EntityStatus.ACTIVED.toString());
            comment.setUpdated(System.currentTimeMillis());
            commentRepo.save(comment);
        }
    }


}
