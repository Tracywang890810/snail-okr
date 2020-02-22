package com.seblong.okr.controllers.manage;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.seblong.okr.entities.OKRPeriod;
import com.seblong.okr.resource.StandardListResource;
import com.seblong.okr.resource.StandardRestResource;
import com.seblong.okr.services.OKRPeriodService;

@Controller
@RequestMapping(value = "/manage/period", produces = MediaType.APPLICATION_JSON_VALUE)
public class APIOKRPeriodManageController {

	@Autowired
	private OKRPeriodService okrPeriodService;

	@GetMapping(value = "/get")
	public ResponseEntity<StandardRestResource> get(int year) {

		List<OKRPeriod> okrPeriods = okrPeriodService.get(year);
		if (okrPeriods == null) {
			okrPeriods = Collections.emptyList();
		}
		return new ResponseEntity<StandardRestResource>(new StandardListResource<OKRPeriod>(okrPeriods), HttpStatus.OK);
	}
	
	@PostMapping(value = "/create")
	public ResponseEntity<StandardRestResource> createt(int year) {

		List<OKRPeriod> okrPeriods = okrPeriodService.create(year);
		if (okrPeriods == null) {
			okrPeriods = Collections.emptyList();
		}
		return new ResponseEntity<StandardRestResource>(new StandardListResource<OKRPeriod>(okrPeriods), HttpStatus.OK);
	}
}
