package com.seblong.okr.services.impl;

import com.seblong.okr.entities.Company;
import com.seblong.okr.repositories.*;
import com.seblong.okr.services.AligningService;
import com.seblong.okr.services.CommentService;
import com.seblong.okr.services.CompanyService;
import com.seblong.okr.services.OKRService;
import com.seblong.okr.utils.XmlUtil;
import com.seblong.okr.utils.wx.AesException;
import com.seblong.okr.utils.wx.WXBizMsgCrypt;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private AligningRepository aligningRepo;

    @Autowired
    private FollowRepository followRepo;

    @Autowired
    private OKRService okrService;

    @Value("${snail.okr.wechat.suite_id}")
    private String suite_id;

    @Value("${snail.okr.wechat.suite_token}")
    private String token;

    @Value("${snail.okr.wechat.suite_encodingAesKey}")
    private String encodingAesKey;

    private final String SUITE_TICKET_KEY = "SUITE_TICKET_KEY";

    @Override
    public Company get(String companyId) {
        return companyRepo.findById(new ObjectId(companyId)).orElse(null);
    }

    @Override
    public void cleanData(String companyId){
        followRepo.deleteByCompanyId(companyId);
        aligningRepo.deleteByCompanyId(companyId);
        okrService.deleteAll(companyId);
        commentRepo.deleteByCompanyId(companyId);
        employeeRepo.deleteByCorpId(companyId);
    }
}
