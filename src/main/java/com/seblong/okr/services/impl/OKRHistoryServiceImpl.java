package com.seblong.okr.services.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.seblong.okr.entities.OKR;
import com.seblong.okr.entities.OKRHistory;
import com.seblong.okr.repositories.OKRHistoryRepository;
import com.seblong.okr.services.OKRHistoryDateService;
import com.seblong.okr.services.OKRHistoryService;
import com.seblong.okr.utils.RedisLock;

@Service
public class OKRHistoryServiceImpl implements OKRHistoryService {

	@Autowired
	private OKRHistoryRepository okrHistoryRepo;

	@Autowired
	private OKRHistoryDateService okrHistoryDateService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private ThreadPoolTaskExecutor executor;

	@Override
	public void create(OKR okr) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				LocalDate localDate = LocalDate.now();
				String date = localDate.format(DateTimeFormatter.BASIC_ISO_DATE);
				RedisLock redisLock = new RedisLock(redisTemplate, "OKR::HISTORY::" + okr.getUser());
				try {
					redisLock.lock();
					OKRHistory okrHistory = okrHistoryRepo.findByUserAndPeriodAndDate(okr.getUser(), okr.getPeriod(),
							date);
					if (okrHistory == null) {
						okrHistory = new OKRHistory(date, okr);
					} else {
						okrHistory.setOkr(okr);
						okrHistory.setCreated(System.currentTimeMillis());
					}
					okrHistoryRepo.save(okrHistory);
					okrHistoryDateService.add(okr.getUser(), okr.getEnterpriseId(), okr.getPeriod(), date);
				} catch (InterruptedException e) {
				} finally {
					redisLock.unlock();
				}
			}
		});
	}

	@Override
	public OKRHistory get(String user, String period, String date) {
		return okrHistoryRepo.findByUserAndPeriodAndDate(user, period, date);
	}

}