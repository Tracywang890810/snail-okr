package com.seblong.okr.services;

import com.seblong.okr.entities.EnterprisePeriodConfig;
import com.seblong.okr.entities.OKRPeriod.Type;
import com.seblong.okr.exceptions.ValidationException;

public interface EnterprisePeriodConfigService {

	EnterprisePeriodConfig create( String enterpriseId, Type type, String startDate, boolean year ) throws ValidationException;
	
	EnterprisePeriodConfig update( String enterpriseId, Type type, String startDate, boolean year ) throws ValidationException;
	
	EnterprisePeriodConfig get( String enterpriseId );
	
	void scheduledCreatePeriod();
}
