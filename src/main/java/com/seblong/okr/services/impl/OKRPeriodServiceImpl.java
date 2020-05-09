package com.seblong.okr.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.seblong.okr.entities.OKRPeriod;
import com.seblong.okr.entities.OKRPeriod.Type;
import com.seblong.okr.repositories.OKRPeriodRepository;
import com.seblong.okr.services.OKRPeriodService;

@Service
public class OKRPeriodServiceImpl implements OKRPeriodService {

	@Autowired
	private OKRPeriodRepository okrPeriodRepo;

	@Override
	public OKRPeriod create(String enterpriseId, Type type, String startDate, long start) {

		if (okrPeriodRepo.countByEnterpriseIdAndTypeAndStartDate(enterpriseId, type, startDate) <= 0) {
			OKRPeriod period = OKRPeriod.build(enterpriseId, type, startDate, start);
			return okrPeriodRepo.save(period);
		}

		return null;
	}

	@Override
	public List<OKRPeriod> list(String enterpriseId, String year) {
		List<OKRPeriod> okrPeriods = okrPeriodRepo.findByEnterpriseIdAndYear(enterpriseId, year);
		if (!CollectionUtils.isEmpty(okrPeriods)) {
			okrPeriods.sort((p1, p2) -> {
				if (p1.getType().equals(Type.YEAR)) {
					return -1;
				} else if (p2.getType().equals(Type.YEAR)) {
					return 1;
				} else {
					return p2.getStart() - p1.getStart() > 0 ? 1 : -1;
				}

			});
		}
		return okrPeriods;
	}

}
