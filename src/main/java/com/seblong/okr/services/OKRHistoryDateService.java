package com.seblong.okr.services;

import com.seblong.okr.entities.OKRHistoryDate;

public interface OKRHistoryDateService {

	OKRHistoryDate add(String user, String enterpriseId, String period, String date);
	
	OKRHistoryDate get(String user, String period);
}