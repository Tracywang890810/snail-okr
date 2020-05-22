package com.seblong.okr.controllers;

import com.seblong.okr.entities.Company;
import com.seblong.okr.services.CompanyService;
import com.seblong.okr.services.CorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/jsapi", produces = MediaType.APPLICATION_JSON_VALUE)
public class APIJSAPIController {

    @Autowired
    private CorpService corpService;

    @Autowired
    private CompanyService companyService;

    @GetMapping("/getCorpTicket")
    public Map<String, Object> getJSAPICorpTicket(@RequestParam(value = "companyId") String companyId){
        Company company = companyService.get(companyId);
        Map<String, Object> rMap = new HashMap<>(4);
        rMap.put("jsapiTicket", corpService.getJSAPICorpTicket(company.getCorpId(), company.getPermanentCode()));
        rMap.put("status", 200);
        rMap.put("message", "OK");
        return rMap;
    }

    @GetMapping("/getAgentTicket")
    public Map<String, Object> getJSAPIAgentTicket(@RequestParam(value = "companyId") String companyId){
        Company company = companyService.get(companyId);
        Map<String, Object> rMap = new HashMap<>(4);
        rMap.put("jsapiTicket", corpService.getJSAPIAgentTicket(company.getCorpId()));
        rMap.put("status", 200);
        rMap.put("message", "OK");
        return rMap;
    }

    @PostMapping("/getSign")
    public Map<String, Object> getJSAPISign(
            @RequestParam(value = "companyId") String companyId,
            @RequestParam(value = "url") String url,
            @RequestParam(value = "type") String type){
        Company company = companyService.get(companyId);
        Map<String, Object> rMap = new HashMap<>(4);
        rMap.put("signMap", corpService.jsSign(url, company.getCorpId(), company.getPermanentCode(), type));
        rMap.put("status", 200);
        rMap.put("message", "OK");
        return rMap;
    }
}
