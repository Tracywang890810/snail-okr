package com.seblong.okr.services.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.seblong.okr.entities.OKRPeriod;
import com.seblong.okr.repositories.OKRPeriodRepository;
import com.seblong.okr.services.OKRPeriodService;

@Service
public class OKRPeriodServiceImpl implements OKRPeriodService {

	@Autowired
	private OKRPeriodRepository okrPeriodRepo;

	private final static ConcurrentHashMap<Integer, List<OKRPeriod>> OKR_PERIOD_YEAR = new ConcurrentHashMap<Integer, List<OKRPeriod>>();

	private final static ConcurrentHashMap<String, OKRPeriod> OKR_PERIOD = new ConcurrentHashMap<String, OKRPeriod>();
	
	@Override
	public List<OKRPeriod> create(int year) {
		if (exist(year)) {
			return get(year);
		}
		List<OKRPeriod> okrPeriods = OKRPeriod.build(year);
		return okrPeriodRepo.saveAll(okrPeriods);
	}
	
	@Override
	public OKRPeriod get(String id) {
		
		if( ObjectId.isValid(id) ) {
			OKRPeriod okrPeriod = OKR_PERIOD.get(id);
			if( okrPeriod == null ) {
				Optional<OKRPeriod> optional = okrPeriodRepo.findById(new ObjectId(id));
				if( optional.isPresent() ) {
					OKR_PERIOD.put(id, optional.get());
					okrPeriod = optional.get();
				}
				return okrPeriod;
			}
		}
		return null;
	}

	@Override
	public List<OKRPeriod> get(int year) {
		
		List<OKRPeriod> periods = OKR_PERIOD_YEAR.get(year);
		if( CollectionUtils.isEmpty(periods) ) {
			periods =  okrPeriodRepo.findByYear(year);
			if( !CollectionUtils.isEmpty(periods) ) {
				OKR_PERIOD_YEAR.put(year, periods);
			}
		}
		return periods;
		
	}

	@Override
	public boolean exist(int year) {
		return okrPeriodRepo.countByYear(year) > 0;
	}

	@Scheduled(cron = "0 50 12 31 12 ?")
	@Override
	public void add() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		int year = calendar.get(Calendar.YEAR);
		create(year);
	}

}
