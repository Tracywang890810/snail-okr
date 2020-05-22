package com.seblong.okr.controllers;

import com.seblong.okr.resource.StandardRestResource;
import com.seblong.okr.services.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/company", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class APICompanyController {

    protected static final Logger logger = LoggerFactory.getLogger(APICompanyController.class);

    @Autowired
    private CompanyService companyService;

    /**
     *
     * @param status
     * @return
     */
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StandardRestResource> list(
            @RequestParam(value = "status") String status){

        return null;
    }

}
