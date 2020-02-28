package com.seblong.okr.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.seblong.okr.entities.OKR;
import com.seblong.okr.entities.OKR.KeyResult;
import com.seblong.okr.entities.OKR.Objective;
import com.seblong.okr.exceptions.ValidationException;
import com.seblong.okr.repositories.OKRRepository;
import com.seblong.okr.services.OKRHistoryService;
import com.seblong.okr.services.OKRService;

@Service
public class OKRServiceImpl implements OKRService {

	@Autowired
	private OKRHistoryService okrHistoryService;

	@Autowired
	private OKRRepository okrRepo;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public OKR get(String user, String period) {
		OKR okr = getOKR(user, period);
		if (okr == null) {
			okr = okrRepo.findByUserAndPeriod(user, period);
			if (okr != null) {
				putOKR(okr);
			}
		}
		return okr;
	}

	@Override
	public List<Objective> listObjectives(String user, String period) {
		OKR okr = get(user, period);
		if (okr != null) {
			return okr.getObjectives();
		}
		return null;
	}

	@Override
	public Objective addObjective(String user, String period, String title, long estimate, double confidence) {
		OKR okr = get(user, period);
		if (okr == null) {
			okr = new OKR(user, period);
		}
		Objective objective = new Objective(title, estimate, confidence);
		okr.addObjective(objective);
		okr = okrRepo.save(okr);
		putOKR(okr);
		okrHistoryService.create(okr);
		return objective;
	}

	@Override
	public Objective getObjective(String user, String period, String id) {
		Objective objective = getObjective(id);
		if (objective == null) {
			OKR okr = get(user, period);
			for (Objective o : okr.getObjectives()) {
				if (o.getId().toString().equals(id)) {
					objective = o;
					break;
				}
			}
			if (objective != null) {
				putObjective(objective);
			}
		}
		return objective;
	}

	@Override
	public Objective updateObjective(String user, String period, String id, String title, long estimate,
			double confidence) throws ValidationException {
		OKR okr = get(user, period);
		Objective objective = null;
		for (Objective o : okr.getObjectives()) {
			if (o.getId().toString().equals(id)) {
				objective = o;
				break;
			}
		}
		if (objective != null) {
			objective.setTitle(title);
			objective.setEstimate(estimate);
			objective.setConfidence(confidence);
			objective.setUpdated(System.currentTimeMillis());
			okr = okrRepo.save(okr);
			putOKR(okr);
			clearObjective(objective);
			okrHistoryService.create(okr);
			return objective;
		}
		return null;
	}

	@Override
	public void deleteObjective(String user, String period, String id) {
		OKR okr = get(user, period);
		if (okr == null) {
			throw new ValidationException(404, "objective-not-exist");
		}
		for (Objective o : okr.getObjectives()) {
			if (o.getId().toString().equals(id)) {
				okr.getObjectives().remove(o);
				clearOKR(okr, o);
				okr = okrRepo.save(okr);
				okrHistoryService.create(okr);
				break;
			}
		}
	}

	@Override
	public Objective addKeyResult(String user, String period, String objective, String title, long estimate,
			double confidence, double weight) throws ValidationException {
		OKR okr = get(user, period);
		if (okr == null) {
			throw new ValidationException(404, "objective-not-exist");
		}
		Objective object = null;
		for (Objective o : okr.getObjectives()) {
			if (o.getId().toString().equals(objective)) {
				object = o;
				break;
			}
		}
		if (object == null) {
			throw new ValidationException(404, "objective-not-exist");
		}
		if (estimate > 0 && object.getEstimate() > 0 && estimate > object.getEstimate()) {
			throw new ValidationException(400, "estimate-exceed");
		}
		object.setUpdated(System.currentTimeMillis());
		object.addKeyResult(new KeyResult(title, estimate, confidence, weight));
		validateWeight(object);
		object.calculateScore();
		okr = okrRepo.save(okr);
		clearObjective(object);
		putOKR(okr);
		okrHistoryService.create(okr);
		return object;
	}

	@Override
	public Objective updateKeyResult(String user, String period, String objective, String id, String title,
			double score, long estimate, double confidence, double weight) throws ValidationException {
		OKR okr = get(user, period);
		if (okr == null) {
			throw new ValidationException(404, "objective-not-exist");
		}
		Objective object = null;
		for (Objective o : okr.getObjectives()) {
			if (o.getId().toString().equals(objective)) {
				object = o;
				break;
			}
		}
		if (object == null) {
			throw new ValidationException(404, "objective-not-exist");
		}
		KeyResult keyResult = null;
		for (KeyResult kr : object.getKeyResults()) {
			if (kr.getId().toString().equals(id)) {
				keyResult = kr;
				break;
			}
		}
		if (keyResult == null) {
			throw new ValidationException(404, "keyresult-not-exist");
		}

		if (estimate > 0 && object.getEstimate() > 0 && estimate > object.getEstimate()) {
			throw new ValidationException(400, "estimate-exceed");
		}
		keyResult.setTitle(title);
		keyResult.setScore(score);
		keyResult.setEstimate(estimate);
		keyResult.setConfidence(confidence);
		keyResult.setWeight(weight);
		keyResult.setUpdated(System.currentTimeMillis());
		object.setUpdated(System.currentTimeMillis());
		validateWeight(object);
		object.calculateScore();
		okr = okrRepo.save(okr);
		putOKR(okr);
		clearObjective(object);
		okrHistoryService.create(okr);
		return object;
	}

	@Override
	public void deleteKeyResult(String user, String period, String objective, String id) {
		OKR okr = get(user, period);
		if (okr == null) {
			throw new ValidationException(404, "objective-not-exist");
		}
		Objective object = null;
		for (Objective o : okr.getObjectives()) {
			if (o.getId().toString().equals(objective)) {
				object = o;
				break;
			}
		}
		if (object == null) {
			throw new ValidationException(404, "objective-not-exist");
		}
		for (KeyResult kr : object.getKeyResults()) {
			if (kr.getId().toString().equals(id)) {
				object.getKeyResults().remove(kr);
				object.calculateScore();
				okr = okrRepo.save(okr);
				putOKR(okr);
				clearObjective(object);
				okrHistoryService.create(okr);
				break;
			}
		}
	}

	@Override
	public void updateProgress(String user, String period, String objective, String progress)
			throws ValidationException {
		OKR okr = get(user, period);
		if (okr == null) {
			throw new ValidationException(404, "objective-not-exist");
		}
		Objective object = null;
		for (Objective o : okr.getObjectives()) {
			if (o.getId().toString().equals(objective)) {
				object = o;
				break;
			}
		}
		if (object == null) {
			throw new ValidationException(404, "objective-not-exist");
		}
		object.setProgress(progress);
		object.setUpdated(System.currentTimeMillis());
		okr = okrRepo.save(okr);
		clearOKR(okr, object);
		okrHistoryService.create(okr);
	}

	@Override
	public Objective scoreKeyResult(String user, String period, String objective, String id, double score)
			throws ValidationException {
		OKR okr = get(user, period);
		if (okr == null) {
			throw new ValidationException(404, "objective-not-exist");
		}
		Objective object = null;
		for (Objective o : okr.getObjectives()) {
			if (o.getId().toString().equals(objective)) {
				object = o;
				break;
			}
		}
		if (object == null) {
			throw new ValidationException(404, "objective-not-exist");
		}
		KeyResult keyResult = null;
		for (KeyResult kr : object.getKeyResults()) {
			if (kr.getId().toString().equals(id)) {
				keyResult = kr;
				break;
			}
		}
		if (keyResult == null) {
			throw new ValidationException(404, "keyresult-not-exist");
		}
		keyResult.setScore(score);
		keyResult.setUpdated(System.currentTimeMillis());
		object.calculateScore();
		object.setUpdated(System.currentTimeMillis());
		okr = okrRepo.save(okr);
		clearObjective(object);
		putOKR(okr);
		okrHistoryService.create(okr);
		return object;
	}

	public void validateWeight(Objective objective) {

		if (!CollectionUtils.isEmpty(objective.getKeyResults())) {
			double total = 0;
			for (KeyResult keyResult : objective.getKeyResults()) {
				total += keyResult.getWeight();
			}
			if (total > 1) {
				throw new ValidationException(400, "weight-exceed");
			}
		}

	}

	private void putObjective(Objective objective) {

		String key = "OKR::OBJECTIVE::" + objective.getId().toString();
		redisTemplate.boundValueOps(key).set(objective, 3600, TimeUnit.SECONDS);

	}

	private Objective getObjective(String id) {
		String key = "OKR::OBJECTIVE::" + id;
		Object object = redisTemplate.boundValueOps(key).get();
		if (object != null) {
			return (Objective) object;
		}
		return null;
	}

	private void clearObjective(Objective objective) {
		String key = "OKR::OBJECTIVE::" + objective.getId().toString();
		redisTemplate.delete(key);
	}

	private void putOKR(OKR okr) {
		String key = "OKR::USER::" + okr.getUser() + "::PERIOD::" + okr.getPeriod();
		redisTemplate.boundValueOps(key).set(okr, 3600, TimeUnit.SECONDS);

	}

	private OKR getOKR(String user, String period) {
		String key = "OKR::USER::" + user + "::PERIOD::" + period;
		Object object = redisTemplate.boundValueOps(key).get();
		if (object != null) {
			return (OKR) object;
		}
		return null;
	}

	private void clearOKR(OKR okr, Objective objective) {
		List<String> keys = new ArrayList<String>(2);
		keys.add("OKR::USER::" + okr.getUser() + "::PERIOD::" + okr.getPeriod());
		if (objective != null) {
			keys.add("OKR::OBJECTIVE::" + objective.getId().toString());
		}
		redisTemplate.delete(keys);
	}

}
