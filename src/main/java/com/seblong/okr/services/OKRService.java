package com.seblong.okr.services;

import java.util.List;

import com.seblong.okr.entities.OKR.KeyResult;
import com.seblong.okr.entities.OKR.Objective;
import com.seblong.okr.exceptions.ValidationException;

public interface OKRService {

	Objective getObjective(String user, String period, String id);

	List<Objective> listObjectives(String user, String period);

	Objective addObjective(String user, String enterpriseId, String period, String title);

	Objective updateObjective(String user, String period, String id, String title, long estimate, int confidence)
			throws ValidationException;

	Objective deleteObjective(String user, String period, String id);
	
	void rankObjective(String user, String period, int oldRank, int newRank);

	KeyResult addKeyResult(String user, String period, String objective, String title) throws ValidationException;

	KeyResult updateKeyResult(String user, String period, String objective, String id, String title,long estimate, int confidence, int weight, int progress) throws ValidationException;

	KeyResult deleteKeyResult(String user, String period, String objective, String id);

	void updateProgress(String user, String period, String objective, String progress) throws ValidationException;

	Objective scoreKeyResult(String user, String period, String objective, String id, int score)
			throws ValidationException;
	
	void rankKeyResult(String user, String period, String objective, int oldRank, int newRank);
	
	void deleteAll(String companyId);
}
