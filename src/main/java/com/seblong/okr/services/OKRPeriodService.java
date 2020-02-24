package com.seblong.okr.services;

import java.util.List;

import com.seblong.okr.entities.OKRPeriod;

public interface OKRPeriodService {

	List<OKRPeriod> create(int year);
	
	List<OKRPeriod> get(int year);
	
	boolean exist(int year);
	
	void add();
}
