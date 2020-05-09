package com.seblong.okr.services;

import java.util.List;

import com.seblong.okr.entities.OKRPeriod;
import com.seblong.okr.entities.OKRPeriod.Type;

public interface OKRPeriodService {

	/**
	 * 创建企业的周期
	 * @param enterpriseId
	 * @param year
	 * @param type
	 * @return
	 */
	OKRPeriod create(String enterpriseId, Type type, String startDate, long start);

	/**
	 * 获取企业的周期列表
	 * @param enterpriseId
	 * @param year
	 * @return
	 */
	List<OKRPeriod> list(String enterpriseId, String year);

}
