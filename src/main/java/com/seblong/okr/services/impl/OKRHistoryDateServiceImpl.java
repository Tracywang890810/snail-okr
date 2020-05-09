package com.seblong.okr.services.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.seblong.okr.entities.OKRHistoryDate;
import com.seblong.okr.repositories.OKRHistoryDateRepository;
import com.seblong.okr.services.OKRHistoryDateService;
import com.seblong.okr.utils.RedisLock;

@Service
public class OKRHistoryDateServiceImpl implements OKRHistoryDateService {

	@Autowired
	private OKRHistoryDateRepository okrHistoryDateRepo;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Override
	public OKRHistoryDate add(String user, String period, String date) {
		RedisLock redisLock = new RedisLock(redisTemplate, "OKR::HISTORY:DATE::ADD::" + user);
		try {
			redisLock.lock();
			OKRHistoryDate okrHistoryDate = okrHistoryDateRepo.findByUserAndPeriod(user, period);
			if( okrHistoryDate == null ) {
				okrHistoryDate = new OKRHistoryDate(user, period);
			}
			if(okrHistoryDate.addDate(date)) {
				okrHistoryDate = okrHistoryDateRepo.save(okrHistoryDate);
			}
			return okrHistoryDate;
		} catch (InterruptedException e) {
		}finally {
			redisLock.unlock();
		}

		return null;
	}

	@Override
	public OKRHistoryDate get(String user, String period) {
		return okrHistoryDateRepo.findByUserAndPeriod(user, period);
	}

}