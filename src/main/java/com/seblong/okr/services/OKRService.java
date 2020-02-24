package com.seblong.okr.services;

import java.util.List;

import com.seblong.okr.entities.OKR;
import com.seblong.okr.entities.OKR.Objective;
import com.seblong.okr.exceptions.ValidationException;

public interface OKRService {

	OKR get(String user, String period);
	
	List<Objective> listObjectives(String user, String period);

	Objective addObjective(String user, String period, String title, long estimate, double confidence);

	Objective getObjective(String user, String period, String id);

	Objective updateObjective(String user, String period, String id, String title, long estimate, double confidence)
			throws ValidationException;

	void deleteObjective(String user, String period, String id);

	Objective addKeyResult(String user, String period, String objective, String title, long estimate, double confidence,
			double weight) throws ValidationException;

	Objective updateKeyResult(String user, String period, String objective, String id, String title, double score,
			long estimate, double confidence, double weight) throws ValidationException;

	void deleteKeyResult(String user, String period, String objective, String id);

	void updateProgress(String user, String period, String objective, String progress) throws ValidationException;

	Objective scoreKeyResult(String user, String period, String objective, String id, double score)
			throws ValidationException;

}
