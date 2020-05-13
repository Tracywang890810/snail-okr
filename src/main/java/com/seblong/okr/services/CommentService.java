package com.seblong.okr.services;

import com.seblong.okr.entities.Comment;

import java.util.List;

public interface CommentService {
    Comment comment(String employeeId, String period, String ownerId, String content);

    void remove(String unique);

    void solve(String unique);

    List<Comment> list(String employeeId, String period, String status);

    void reopen(String unique);
}
