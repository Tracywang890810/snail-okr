package com.seblong.okr.services.impl;

import com.seblong.okr.entities.Comment;
import com.seblong.okr.entities.Company;
import com.seblong.okr.entities.Employee;
import com.seblong.okr.enums.EntityStatus;
import com.seblong.okr.repositories.CommentRepository;
import com.seblong.okr.repositories.CompanyRepository;
import com.seblong.okr.services.CommentService;
import com.seblong.okr.services.CompanyService;
import com.seblong.okr.services.EmployeeService;
import com.seblong.okr.services.MessageService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private MessageService messageService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompanyRepository companyRepo;

    @Value("${snail.okr.wechat.url.redirect}")
    private String redirectUrl;

    @Override
    public Comment comment(String employeeId, String period, String ownerId, String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setEmployee(employeeId);
        comment.setOwner(ownerId);
        comment.setPeriod(period);
        comment = commentRepo.save(comment);
        Employee employee = employeeService.findById(employeeId);
        Employee owner = employeeService.findById(ownerId);
        Company company = companyRepo.findByCorpId(owner.getCorpId());
        String description = "$userName=" + employee.getUserId() + "$关注了您。";
        messageService.sendMessageToUser(owner.getUserId(), owner.getCorpId(), company.getPermanentCode(), company.getAgentId(), "评论通知", description, redirectUrl, null, null, null, null);
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
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        return commentRepo.findByPeriodAndOwnerAndStatus(period, employeeId, status, sort);
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
