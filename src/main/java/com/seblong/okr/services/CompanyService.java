package com.seblong.okr.services;

import com.seblong.okr.entities.Company;
import com.seblong.okr.utils.wx.AesException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CompanyService {
    Company get(String companyId);

    void cleanData(String companyId);

    Page<Company> list(PageRequest pageRequest);

    boolean licenseCompany(String unique, long end);

    boolean disableCompany(String unique);
}
