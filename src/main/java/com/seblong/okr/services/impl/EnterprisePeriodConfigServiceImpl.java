package com.seblong.okr.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.seblong.okr.entities.EnterprisePeriodConfig;
import com.seblong.okr.entities.OKRPeriod.Type;
import com.seblong.okr.exceptions.ValidationException;
import com.seblong.okr.repositories.EnterprisePeriodConfigRepository;
import com.seblong.okr.services.EnterprisePeriodConfigService;
import com.seblong.okr.services.OKRPeriodService;
import com.seblong.okr.utils.RedisLock;

@Service
public class EnterprisePeriodConfigServiceImpl implements EnterprisePeriodConfigService {

	@Autowired
	private EnterprisePeriodConfigRepository enterprisePeriodConfigRepo;

	@Autowired
	private OKRPeriodService okrPeriodService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public EnterprisePeriodConfig create(String enterpriseId, Type type, String startDate, boolean year)
			throws ValidationException {
		RedisLock redisLock = new RedisLock(redisTemplate, "PERIOD::CONFIG::" + enterpriseId);
		try {
			redisLock.lock();
			EnterprisePeriodConfig enterprisePeriodConfig = get(enterpriseId);
			if (enterprisePeriodConfig != null)
				throw new ValidationException(1405, "config-exist");

			long start = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE).atStartOfDay()
					.toEpochSecond(ZoneOffset.ofHours(8)) * 1000;
			enterprisePeriodConfig = EnterprisePeriodConfig.build(enterpriseId, type, startDate, year, start);

			if (enterprisePeriodConfig.isNeedCreated()) {
				// 创建当前period
				okrPeriodService.create(enterpriseId, type, startDate, start);
			}
			if (year) {
				// 创建年度period
				startDate = startDate.substring(0, 4) + "0101";
				start = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE).atStartOfDay()
						.toEpochSecond(ZoneOffset.ofHours(8)) * 1000;
				okrPeriodService.create(enterpriseId, Type.YEAR, startDate, start);
			}
			return enterprisePeriodConfigRepo.save(enterprisePeriodConfig);
		} catch (InterruptedException e) {
			throw new ValidationException(1500, "system-error");
		} finally {
			redisLock.unlock();
		}

	}

	@Override
	public EnterprisePeriodConfig update(String enterpriseId, Type type, String startDate, boolean year)
			throws ValidationException {
		EnterprisePeriodConfig enterprisePeriodConfig = get(enterpriseId);
		if (enterprisePeriodConfig == null)
			throw new ValidationException(1404, "config-not-exist");

		boolean updateStart = false;

		if (enterprisePeriodConfig.getType().equals(type)) {
			if (!enterprisePeriodConfig.getStartDate().equals(startDate)) {
				updateStart = true;
			}
		} else {
			enterprisePeriodConfig.setType(type);
			updateStart = true;
		}
		if (updateStart) {
			LocalDateTime startLocalDateTime = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE)
					.atStartOfDay();
			long start = startLocalDateTime.toEpochSecond(ZoneOffset.ofHours(8)) * 1000;
			enterprisePeriodConfig.setStart(start);
			enterprisePeriodConfig.setStartDate(startDate);
			EnterprisePeriodConfig.calculateNext(enterprisePeriodConfig);
			if (enterprisePeriodConfig.isNeedCreated()) {
				// 创建当前的period
				okrPeriodService.create(enterpriseId, type, startDate, start);
			}
		}
		if (!enterprisePeriodConfig.isYear() && year) {
			// 创建年度period
			startDate = startDate.substring(0, 5) + "0101";
			long start = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE).atStartOfDay()
					.toEpochSecond(ZoneOffset.ofHours(8)) * 1000;
			okrPeriodService.create(enterpriseId, Type.YEAR, startDate, start);
		}
		enterprisePeriodConfig.setYear(year);

		return enterprisePeriodConfigRepo.save(enterprisePeriodConfig);
	}

	@Override
	public EnterprisePeriodConfig get(String enterpriseId) {
		return enterprisePeriodConfigRepo.findByEnterpriseId(enterpriseId);
	}

	@Scheduled(cron = "0 10 0 24 * ?")
	@Override
	public void scheduledCreatePeriod() {

		List<EnterprisePeriodConfig> configs = enterprisePeriodConfigRepo.findAll();
		List<EnterprisePeriodConfig> needCreateConfigs = configs.stream().filter(EnterprisePeriodConfig::isNeedCreated)
				.collect(Collectors.toList());
		needCreateConfigs.forEach(config -> {
			// 创建周期
			okrPeriodService.create(config.getEnterpriseId(), config.getType(), config.getNextDate(), config.getNext());
			//计算下一次的周期开始时间
			LocalDate startLocalDate = LocalDate.parse(config.getNextDate(), DateTimeFormatter.BASIC_ISO_DATE);
			LocalDate nextLocalDate = startLocalDate.plusMonths(config.getType().num);
			nextLocalDate = nextLocalDate.with(TemporalAdjusters.firstDayOfMonth());
			long next = nextLocalDate.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(8)) * 1000;
			config.setNext(next);
			config.setNextDate(nextLocalDate.format(DateTimeFormatter.BASIC_ISO_DATE));
		});
		enterprisePeriodConfigRepo.saveAll(needCreateConfigs);

	}

}
