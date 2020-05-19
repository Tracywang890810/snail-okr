package com.seblong.okr.services.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.seblong.okr.entities.OKR;
import com.seblong.okr.entities.OKR.KeyResult;
import com.seblong.okr.entities.OKR.Objective;
import com.seblong.okr.exceptions.ValidationException;
import com.seblong.okr.repositories.OKRHistoryDateRepository;
import com.seblong.okr.repositories.OKRHistoryRepository;
import com.seblong.okr.repositories.OKRPeriodRepository;
import com.seblong.okr.repositories.OKRRepository;
import com.seblong.okr.services.OKRHistoryService;
import com.seblong.okr.services.OKRService;
import com.seblong.okr.utils.OKRCache;
import com.seblong.okr.utils.ObjectiveCache;

@Service
public class OKRServiceImpl implements OKRService {

	@Autowired
	private OKRRepository okrRepo;
	
	@Autowired
	private OKRPeriodRepository okrPeriodRepo;
	
	@Autowired
	private OKRHistoryRepository okrHistoryRepo;
	
	@Autowired
	private OKRHistoryDateRepository okrHistoryDateRepo;
	

	@Autowired
	private OKRHistoryService okrHistoryService;

	@Autowired
	private ThreadPoolTaskExecutor executor;

	private final OKRCache CACHE_OKR;

	private final ObjectiveCache CACHE_OBJECTIVE;

	@Autowired
	public OKRServiceImpl(RedisTemplate<String, Object> redisTemplate) {
		CACHE_OKR = new OKRCache(100, redisTemplate);
		CACHE_OBJECTIVE = new ObjectiveCache(100, redisTemplate);
	}

	@Override
	public Objective getObjective(String user, String period, String id) {
		Objective objective = CACHE_OBJECTIVE.get(id);
		if (objective == null) {
			OKR okr = getOKR(user, period);
			objective = okr.findObjective(new ObjectId(id));
			if (objective != null)
				CACHE_OBJECTIVE.put(objective);
		}
		return objective;
	}

	@Override
	public List<Objective> listObjectives(String user, String period) {
		OKR okr = getOKR(user, period);
		if (okr != null)
			return okr.getObjectives();
		return null;
	}

	@Override
	public Objective addObjective(String user, String enterpriseId, String period, String title) {
		OKR okr = getOKR(user, period);
		if (okr == null){
			okr = new OKR(user, period, enterpriseId);
		}
		Objective objective = new Objective(title);
		okr.addObjective(objective);
		okr = okrRepo.save(okr);
		CACHE_OKR.put(okr);
		okrHistoryService.create(okr);
		return objective;
	}

	@Override
	public Objective updateObjective(String user, String period, String id, String title, long estimate,
			int confidence) throws ValidationException {
		OKR okr = getOKR(user, period);
		Objective objective = null;
		if (okr != null) {
			objective = okr.findObjective(new ObjectId(id));
			if (objective != null) {
				objective.setTitle(title);
				objective.setEstimate(estimate);
				objective.setConfidence(confidence);
				objective.setUpdated(System.currentTimeMillis());
				okr = okrRepo.save(okr);
				CACHE_OBJECTIVE.put(objective);
				CACHE_OKR.put(okr);
				okrHistoryService.create(okr);
			}
		}

		return objective;
	}

	@Override
	public Objective deleteObjective(String user, String period, String id) {
		OKR okr = getOKR(user, period);
		Objective objective = null;
		if (okr != null) {
			objective = okr.findAndRemoveOBjective(new ObjectId(id));
			if (objective != null) {
				CACHE_OBJECTIVE.remove(objective);
				CACHE_OKR.remove(okr);
				okrRepo.save(okr);
				okrHistoryService.create(okr);
			}
		}
		return objective;
	}

	@Override
	public void rankObjective(String user, String period, int oldRank, int newRank) {
		OKR okr = getOKR(user, period);
		if (okr != null) {
			if(okr.rank(oldRank, newRank)){
				okr = okrRepo.save(okr);
				CACHE_OKR.put(okr);
				okrHistoryService.create(okr);
			}
		}
	}

	@Override
	public void updateProgress(String user, String period, String id, String progress) throws ValidationException {
		OKR okr = getOKR(user, period);
		if (okr != null) {
			Objective objective = okr.findObjective(new ObjectId(id));
			if (objective != null) {
				objective.setProgress(progress);
				objective.setUpdated(System.currentTimeMillis());
				okr = okrRepo.save(okr);
				CACHE_OBJECTIVE.remove(objective);
				CACHE_OKR.remove(okr);
				okrHistoryService.create(okr);
			}
		}
	}

	@Override
	public KeyResult addKeyResult(String user, String period, String objectiveId, String title)
			throws ValidationException {
		OKR okr = getOKR(user, period);
		KeyResult keyResult = null;
		if (okr != null) {
			Objective objective = okr.findObjective(new ObjectId(objectiveId));
			if (objective != null) {
				keyResult = new KeyResult(title);
				objective.addKeyResult(keyResult);
				okr = okrRepo.save(okr);
				CACHE_OBJECTIVE.remove(objective);
				CACHE_OKR.remove(okr);
				okrHistoryService.create(okr);
			}
		}
		return keyResult;
	}

	@Override
	public KeyResult updateKeyResult(String user, String period, String objectiveId, String id, String title,
			long estimate, int confidence, int weight, int progress) throws ValidationException {

		OKR okr = getOKR(user, period);
		KeyResult keyResult = null;
		if (okr != null) {
			Objective objective = okr.findObjective(new ObjectId(objectiveId));
			if (objective != null) {
				keyResult = objective.findKeyResult(new ObjectId(id));
				if (keyResult != null) {
					keyResult.setTitle(title);
					keyResult.setEstimate(estimate);
					keyResult.setConfidence(confidence);
					keyResult.setWeight(weight);
					validateWeight(objective);
					keyResult.setProgress(progress);
					keyResult.setUpdated(System.currentTimeMillis());
					objective.setUpdated(System.currentTimeMillis());
					okr = okrRepo.save(okr);
					CACHE_OBJECTIVE.remove(objective);
					CACHE_OKR.remove(okr);
					okrHistoryService.create(okr);
				}
			}
		}
		return keyResult;
	}

	@Override
	public KeyResult deleteKeyResult(String user, String period, String objectiveId, String id) {
		OKR okr = getOKR(user, period);
		KeyResult keyResult = null;
		if (okr != null) {
			Objective objective = okr.findObjective(new ObjectId(objectiveId));
			if (objective != null) {
				keyResult = objective.findAndRemoveKeyResult(new ObjectId(id));
				if (keyResult != null) {
					okrRepo.save(okr);
					CACHE_OBJECTIVE.remove(objective);
					CACHE_OKR.remove(okr);
					okrHistoryService.create(okr);
				}
			}
		}
		return keyResult;
	}

	@Override
	public Objective scoreKeyResult(String user, String period, String objectiveId, String id, int score)
			throws ValidationException {

		OKR okr = getOKR(user, period);
		Objective objective = null;
		if (okr != null) {
			objective = okr.findObjective(new ObjectId(objectiveId));
			if (objective != null) {
				if(objective.scoreKeyResult(new ObjectId(id), score) != null) {
					okr = okrRepo.save(okr);
					CACHE_OBJECTIVE.remove(objective);
					CACHE_OKR.remove(okr);
					okrHistoryService.create(okr);
				}else {
					objective = null;
				}
			}
		}

		return objective;
	}

	@Override
	public void rankKeyResult(String user, String period, String objectiveId, int oldRank, int newRank) {
		OKR okr = getOKR(user, period);
		if (okr != null) {
			Objective objective = okr.findObjective(new ObjectId(objectiveId));
			if( objective != null ) {
				if( objective.rank(oldRank, newRank) ) {
					okr = okrRepo.save(okr);
					CACHE_OKR.put(okr);
					CACHE_OBJECTIVE.put(objective);
					okrHistoryService.create(okr);
				}
			}
		}
	}

	private OKR getOKR(String user, String period) {
		OKR okr = CACHE_OKR.get(user, period);
		if (okr == null) {
			okr = okrRepo.findByUserAndPeriod(user, period);
			if (okr != null) {
				CACHE_OKR.put(okr);
			}
		}
		return okr;
	}

	private void validateWeight(Objective objective) {

		if (!CollectionUtils.isEmpty(objective.getKeyResults())) {
			double total = 0;
			for (KeyResult keyResult : objective.getKeyResults()) {
				total += keyResult.getWeight();
			}
			if (total > 100) {
				throw new ValidationException(400, "weight-exceed");
			}
		}

	}

	@Override
	public void deleteAll(String companyId) {
		executor.execute(() -> {
			okrRepo.deleteByEnterpriseId(companyId);
			okrHistoryRepo.deleteByEnterpriseId(companyId);
			okrHistoryDateRepo.deleteByEnterpriseId(companyId);
			okrPeriodRepo.deleteByEnterpriseId(companyId);
		});
	}

}
