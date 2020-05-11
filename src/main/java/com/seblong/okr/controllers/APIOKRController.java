package com.seblong.okr.controllers;

import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
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

import com.seblong.okr.entities.OKR.KeyResult;
import com.seblong.okr.entities.OKR.Objective;
import com.seblong.okr.entities.OKRHistory;
import com.seblong.okr.entities.OKRHistoryDate;
import com.seblong.okr.exceptions.ValidationException;
import com.seblong.okr.resource.StandardEntityResource;
import com.seblong.okr.resource.StandardListResource;
import com.seblong.okr.resource.StandardRestResource;
import com.seblong.okr.services.OKRHistoryDateService;
import com.seblong.okr.services.OKRHistoryService;
import com.seblong.okr.services.OKRPeriodService;
import com.seblong.okr.services.OKRService;

@Controller
@RequestMapping(value = "/okr", produces = MediaType.APPLICATION_JSON_VALUE)
public class APIOKRController {

	@Autowired
	private OKRService okrService;

	@Autowired
	private OKRPeriodService okrPeriodService;

	@Autowired
	private OKRHistoryService okrHistoryService;

	@Autowired
	private OKRHistoryDateService okrHistoryDateService;

	@PostMapping(value = "/objective/add")
	public ResponseEntity<StandardEntityResource<Objective>> addObjective(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period,
			@RequestParam(value = "title", required = true) String title) {
		validateUser(user);
		validatePeriod(period);
		validateTitle(title);
		Objective objective = okrService.addObjective(user, period, title);
		return new ResponseEntity<>(new StandardEntityResource<>(objective), HttpStatus.OK);
	}

	@GetMapping(value = "/objective/list")
	public ResponseEntity<StandardListResource<Objective>> listObjective(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period) {
		validateUser(user);
		validatePeriod(period);
		List<Objective> objectives = okrService.listObjectives(user, period);
		if (objectives == null) {
			objectives = Collections.emptyList();
		}
		return new ResponseEntity<>(new StandardListResource<>(objectives), HttpStatus.OK);
	}
	
	@GetMapping(value = "/objective/get")
	public ResponseEntity<StandardRestResource> getObjective(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period,
			@RequestParam(value = "id", required = true) String id) {
		validateUser(user);
		validatePeriod(period);
		validateObjective(id);
		Objective objective = okrService.getObjective(user, period, id);
		if (objective == null) {
			throw new ValidationException(1404, "objective-not-exist");
		}
		return new ResponseEntity<>(new StandardEntityResource<>(objective), HttpStatus.OK);
	}

	@PostMapping(value = "/objective/update")
	public  ResponseEntity<StandardEntityResource<Objective>> updateObjective(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "title", required = true) String title,
			@RequestParam(value = "estimate", required = true) long estimate,
			@RequestParam(value = "confidence", required = true) double confidence) {

		validateUser(user);
		validatePeriod(period);
		validateObjective(id);
		validateTitle(title);
		validateConfidence(confidence);
		Objective objective = okrService.updateObjective(user, period, id, title, estimate, confidence);
		if (objective == null) {
			throw new ValidationException(1404, "objective-not-exist");
		}
		return new ResponseEntity<>(new StandardEntityResource<>(objective), HttpStatus.OK);
	}

	@PostMapping(value = "/objective/delete")
	public ResponseEntity<StandardRestResource> deleteObjective(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period,
			@RequestParam(value = "id", required = true) String id) {
		validateUser(user);
		validatePeriod(period);
		validateObjective(id);
		Objective objective = okrService.deleteObjective(user, period, id);
		if (objective == null) {
			throw new ValidationException(1404, "objective-not-exist");
		}
		return new ResponseEntity<StandardRestResource>(new StandardRestResource(200, "OK"), HttpStatus.OK);
	}

	@PostMapping(value = "/objective/rank")
	public ResponseEntity<StandardRestResource> rankObjective(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period,
			@RequestParam(value = "oldRank", required = true) int oldRank,
			@RequestParam(value = "newRank", required = true) int newRank) {
		validateUser(user);
		validatePeriod(period);
		okrService.rankObjective(user, period, oldRank, newRank);
		return new ResponseEntity<StandardRestResource>(new StandardRestResource(200, "OK"), HttpStatus.OK);
	}

	@PostMapping(value = "/objective/progress/update")
	public ResponseEntity<StandardRestResource> updateProgress(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period,
			@RequestParam(value = "objective", required = true) String objective,
			@RequestParam(value = "progress", required = true) String progress) {
		validateUser(user);
		validatePeriod(period);
		validateObjective(objective);
		okrService.updateProgress(user, period, objective, progress);
		return new ResponseEntity<StandardRestResource>(new StandardRestResource(200, "OK"), HttpStatus.OK);
	}
	
	@PostMapping(value = "/keyresult/add")
	public ResponseEntity<StandardEntityResource<KeyResult>> addKeyResult(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period,
			@RequestParam(value = "objective", required = true) String objective,
			@RequestParam(value = "title", required = true) String title) {
		validateUser(user);
		validatePeriod(period);
		validateObjective(objective);
		validateTitle(title);
		KeyResult keyResult = okrService.addKeyResult(user, period, objective, title);
		if (keyResult == null) {
			throw new ValidationException(1404, "objective-not-exist");
		}
		return new ResponseEntity<>(new StandardEntityResource<KeyResult>(keyResult), HttpStatus.OK);
	}

	@PostMapping(value = "/keyresult/update")
	public ResponseEntity<StandardEntityResource<KeyResult>> updateKeyResult(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period,
			@RequestParam(value = "objective", required = true) String objective,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "title", required = true) String title,
			@RequestParam(value = "progress", required = true) int progress,
			@RequestParam(value = "estimate", required = true) long estimate,
			@RequestParam(value = "confidence", required = true) double confidence,
			@RequestParam(value = "weight", required = true) double weight) {
		validateUser(user);
		validatePeriod(period);
		validateObjective(objective);
		validateKeyResult(id);
		validateTitle(title);
		validateConfidence(confidence);
		validateWeight(weight);
		KeyResult keyResult = okrService.updateKeyResult(user, period, objective, id, title, estimate, confidence,
				weight, progress);
		if (keyResult == null) {
			throw new ValidationException(1404, "keyresult-not-exist");
		}
		return new ResponseEntity<>(new StandardEntityResource<KeyResult>(keyResult), HttpStatus.OK);
	}

	@PostMapping(value = "/keyresult/delete")
	public ResponseEntity<StandardRestResource> deleteKeyResult(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period,
			@RequestParam(value = "objective", required = true) String objective,
			@RequestParam(value = "id", required = true) String id) {
		validateUser(user);
		validatePeriod(period);
		validateObjective(objective);
		validateKeyResult(id);
		KeyResult keyResult = okrService.deleteKeyResult(user, period, objective, id);
		if (keyResult == null) {
			throw new ValidationException(1404, "keyresult-not-exist");
		}
		return new ResponseEntity<StandardRestResource>(new StandardRestResource(200, "OK"), HttpStatus.OK);
	}
	
	@PostMapping(value = "/keyresult/rank")
	public ResponseEntity<StandardRestResource> rankObjective(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period,
			@RequestParam(value = "objective", required = true) String objective,
			@RequestParam(value = "oldRank", required = true) int oldRank,
			@RequestParam(value = "newRank", required = true) int newRank) {
		validateUser(user);
		validatePeriod(period);
		validateObjective(objective);
		okrService.rankKeyResult(user, period, objective, oldRank, newRank);
		return new ResponseEntity<StandardRestResource>(new StandardRestResource(200, "OK"), HttpStatus.OK);
	}

	@PostMapping(value = "/keyresult/score")
	public ResponseEntity<StandardEntityResource<Objective>> scoreKeyResult(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period,
			@RequestParam(value = "objective", required = true) String objective,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "score", required = true) double score) {
		validateUser(user);
		validatePeriod(period);
		validateObjective(objective);
		validateKeyResult(id);
		validateScore(score);
		Objective object = okrService.scoreKeyResult(user, period, objective, id, score);
		if (object == null) {
			throw new ValidationException(1404, "keyresult-not-exist");
		}
		return new ResponseEntity<>(new StandardEntityResource<Objective>(object), HttpStatus.OK);
	}

	@GetMapping(value = "/history/date")
	public ResponseEntity<StandardListResource<String>> getHistoryDate(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period) {

		validateUser(user);
		validatePeriod(period);
		OKRHistoryDate okrHistoryDate = okrHistoryDateService.get(user, period);
		List<String> dates = null;
		if (okrHistoryDate == null) {
			dates = Collections.emptyList();
		} else {
			dates = okrHistoryDate.getDates();
		}

		return new ResponseEntity<>(new StandardListResource<String>(dates), HttpStatus.OK);
	}

	@GetMapping(value = "/history")
	public ResponseEntity<StandardListResource<Objective>> getHistory(@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "period", required = true) String period,
			@RequestParam(value = "date", required = true) String date) {
		validateUser(user);
		validatePeriod(period);
		OKRHistory okrHistory = okrHistoryService.get(user, period, date);
		if (okrHistory != null) {
			return new ResponseEntity<>(new StandardListResource<Objective>(okrHistory.getOkr()
					.getObjectives()), HttpStatus.OK);
		}else {
			throw new ValidationException(1404, "history-not-exits");
		}
	}

	private void validateUser(String user) {
		if (!ObjectId.isValid(user)) {
			throw new ValidationException(1401, "invalid-user");
		}
	}

	private void validatePeriod(String period) {
		if (!ObjectId.isValid(period)) {
			throw new ValidationException(1402, "invalid-period");
		}
	}

	private void validateObjective(String objectiveId) {
		if (!ObjectId.isValid(objectiveId)) {
			throw new ValidationException(1403, "invalid-objective");
		}
	}

	private void validateKeyResult(String keyresultId) {
		if (!ObjectId.isValid(keyresultId)) {
			throw new ValidationException(1405, "invalid-keyresult");
		}
	}

	private void validateTitle(String title) {
		if (StringUtils.isEmpty(title)) {
			throw new ValidationException(1406, "invalid-title");
		}
	}

	private void validateConfidence(double confidence) {
		if (confidence < 0 || confidence > 1) {
			throw new ValidationException(1407, "invalid-confidence");
		}
	}

	private void validateScore(double score) {
		if (score < 0 || score > 10) {
			throw new ValidationException(1408, "invalid-score");
		}
	}

	private void validateWeight(double weight) {
		if (weight < 0 || weight > 1) {
			throw new ValidationException(1409, "invalid-weight");
		}
	}

}