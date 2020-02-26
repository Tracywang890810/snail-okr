package com.seblong.okr.services;

import com.seblong.okr.entities.OKR;
import com.seblong.okr.entities.OKRHistory;

public interface OKRHistoryService {

	void create(OKR okr);
	
	OKRHistory get(String user, String period, String date);
}
