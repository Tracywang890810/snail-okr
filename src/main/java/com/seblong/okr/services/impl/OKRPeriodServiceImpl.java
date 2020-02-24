package com.seblong.okr.services.impl;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.seblong.okr.entities.OKRPeriod;
import com.seblong.okr.repositories.OKRPeriodRepository;
import com.seblong.okr.services.OKRPeriodService;

@Service
public class OKRPeriodServiceImpl implements OKRPeriodService {

	@Autowired
	private OKRPeriodRepository okrPeriodRepo;
	
	@Override
	public List<OKRPeriod> create(int year) {
		if( exist(year) ) {
			return get(year);
		}
		List<OKRPeriod> okrPeriods = OKRPeriod.build(year);
		return okrPeriodRepo.saveAll(okrPeriods);
	}

	@Override
	public List<OKRPeriod> get(int year) {
		return okrPeriodRepo.findByYear(year);
	}

	@Override
	public boolean exist(int year) {
		return okrPeriodRepo.countByYear(year) > 0;
	}

	@Scheduled(cron = "0 50 12 31 12 ?")
	@Override
	public void add() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		create(year);
	}

}
