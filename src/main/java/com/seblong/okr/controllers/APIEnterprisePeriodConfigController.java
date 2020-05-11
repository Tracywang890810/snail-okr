package com.seblong.okr.controllers;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.seblong.okr.entities.EnterprisePeriodConfig;
import com.seblong.okr.entities.OKRPeriod.Type;
import com.seblong.okr.exceptions.ValidationException;
import com.seblong.okr.resource.StandardEntityResource;
import com.seblong.okr.services.EnterprisePeriodConfigService;

@Controller
@RequestMapping(value = "/enterprise/period/config", produces = MediaType.APPLICATION_JSON_VALUE)
public class APIEnterprisePeriodConfigController {

	@Autowired
	private EnterprisePeriodConfigService enterprisePeriodConfigService;

	@PostMapping(value = "/create")
	public ResponseEntity<StandardEntityResource<EnterprisePeriodConfig>> create(
			@RequestParam(value = "enterpriseId") String enterpriseId, @RequestParam(value = "type") Type type,
			@RequestParam(value = "startDate") String startDate, @RequestParam(value = "year") boolean year) {

		validate(enterpriseId, type, startDate);
		EnterprisePeriodConfig config = enterprisePeriodConfigService.create(enterpriseId, type, startDate, year);

		return new ResponseEntity<StandardEntityResource<EnterprisePeriodConfig>>(
				new StandardEntityResource<EnterprisePeriodConfig>(config), HttpStatus.OK);
	}

	@PostMapping(value = "/update")
	public ResponseEntity<StandardEntityResource<EnterprisePeriodConfig>> update(
			@RequestParam(value = "enterpriseId") String enterpriseId, @RequestParam(value = "type") Type type,
			@RequestParam(value = "startDate") String startDate, @RequestParam(value = "year") boolean year) {

		validate(enterpriseId, type, startDate);
		EnterprisePeriodConfig config = enterprisePeriodConfigService.update(enterpriseId, type, startDate, year);

		return new ResponseEntity<StandardEntityResource<EnterprisePeriodConfig>>(
				new StandardEntityResource<EnterprisePeriodConfig>(config), HttpStatus.OK);
	}

	@GetMapping(value = "/get")
	public ResponseEntity<StandardEntityResource<EnterprisePeriodConfig>> get(
			@RequestParam(value = "enterpriseId") String enterpriseId) {
		EnterprisePeriodConfig config = enterprisePeriodConfigService.get(enterpriseId);
		if( config == null ) {
			throw new ValidationException(1404, "config-not-exist");
		}
		return new ResponseEntity<StandardEntityResource<EnterprisePeriodConfig>>(
				new StandardEntityResource<EnterprisePeriodConfig>(config), HttpStatus.OK);
	}

	private void validate(String enterpriseId, Type type, String startDate) {
		if (StringUtils.isEmpty(enterpriseId)) {
			throw new ValidationException(1401, "invalid-enterpriseId");
		}
		if (type == null) {
			throw new ValidationException(1402, "invalid-type");
		}

		String regex = EnterprisePeriodConfig.generateStartDateRegex(type);
		if (!Pattern.matches(regex, startDate)) {
			throw new ValidationException(1403, "invalid-startDate");
		}
	}

}
