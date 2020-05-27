package com.seblong.okr.controllers.management;

import com.seblong.okr.entities.Company;
import com.seblong.okr.resource.StandardEntitiesResource;
import com.seblong.okr.resource.StandardRestResource;
import com.seblong.okr.services.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/manage/company", produces = {MediaType.APPLICATION_JSON_VALUE})
public class APICompanyMController {

    protected static final Logger logger = LoggerFactory.getLogger(APICompanyMController.class);

    @Autowired
    private CompanyService companyService;

    /**
     * 获取企业列表
     * @param status
     * @return
     */
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StandardRestResource> list(
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "offset", required = false, defaultValue = "20") int offset){
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        PageRequest pageRequest = PageRequest.of(page - 1, offset, sort);
        Page<Company> pages = companyService.list(pageRequest);
        List<Company> companyList = new ArrayList<>();
        pages.forEach(company -> {
            companyList.add(company);
        });
        return new ResponseEntity<StandardRestResource>(new StandardEntitiesResource<>(companyList, pages.getTotalElements(), pages.hasPrevious(), pages.hasNext()), HttpStatus.OK);
    }

    /**
     * 禁用企业
     * @param unique
     * @return
     */
    @PostMapping("/disable")
    public Map<String, Object> disableCompany(@RequestParam(value = "unique") String unique){
        boolean result = companyService.disableCompany(unique);
        Map<String, Object> rMap = new HashMap<>(2);
        if(result){
            rMap.put("status", 200);
            rMap.put("message", "OK");
        }else {
            rMap.put("status", 405);
            rMap.put("message", "disable-company-error");
        }
        return rMap;
    }

    /**
     * 授权企业
     * @param unique
     * @param end
     * @return
     */
    @PostMapping("/license")
    public Map<String, Object> licenseCompany(
            @RequestParam(value = "unique") String unique,
            @RequestParam(value = "end") long end){
        boolean result = companyService.licenseCompany(unique, end);
        Map<String, Object> rMap = new HashMap<>(2);
        if(result){
            rMap.put("status", 200);
            rMap.put("message", "OK");
        }else {
            rMap.put("status", 405);
            rMap.put("message", "license-company-error");
        }
        return rMap;
    }

}
