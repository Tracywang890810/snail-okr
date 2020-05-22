package com.seblong.okr.services;

import com.seblong.okr.entities.Company;
import com.seblong.okr.utils.wx.AesException;

public interface CompanyService {
    Company get(String companyId);

    void cleanData(String companyId);
}
