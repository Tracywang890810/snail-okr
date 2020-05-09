package com.seblong.okr.entities;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.stream.IntStream;

import lombok.Data;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.seblong.okr.entities.OKRPeriod.Type;
import com.seblong.okr.enums.EntityStatus;

@Document(collection = "c_enterprise_period_configs")
@Data
public class EnterprisePeriodConfig {

	@JsonProperty(value = "unique")
	@JsonSerialize(using = ToStringSerializer.class)
	@Id
	private ObjectId id;

	// 企业id
	@Indexed(unique = true)
	private String enterpriseId;

	@JsonSerialize(using = ToStringSerializer.class)
	// 周期类型
	private Type type;

	// 开始生效时间 yyyyMMdd
	private String startDate;

	@JsonIgnore
	// 开始创建周期的时间
	private long start;

	private boolean year;

	@JsonIgnore
	private String nextDate;

	@JsonIgnore
	// 下次创建周期的时间
	private long next;

	@JsonSerialize(using = ToStringSerializer.class)
	private EntityStatus status;

	private long updated;
	
	@JsonIgnore
	@Transient
	private boolean needCreated;
	
	@PersistenceConstructor
	public EnterprisePeriodConfig(String enterpriseId, Type type, String startDate, boolean year, long start,
			long next, EntityStatus status, long updated) {
		this(enterpriseId, type, startDate, year, start);
		this.next = next;
		this.status = status;
		this.updated = updated;
		this.needCreated = needCreate(next);
	}

	private EnterprisePeriodConfig(String enterpriseId, Type type, String startDate, boolean year, long start) {
		this.enterpriseId = enterpriseId;
		this.type = type;
		this.startDate = startDate;
		this.year = year;
		this.start = start;
		this.status = EntityStatus.ACTIVED;
		this.updated = System.currentTimeMillis();
	}

	public static EnterprisePeriodConfig build(String enterpriseId, Type type, String startDate, boolean year,
			long start) {
		EnterprisePeriodConfig config = new EnterprisePeriodConfig(enterpriseId, type, startDate, year, start);
		calculateNext(config);
		return config;
	}

	public static void calculateNext(EnterprisePeriodConfig config) {
		if( needCreate(config.getStart()) ) {
			LocalDate startLocalDate = LocalDate.parse(config.getStartDate(), DateTimeFormatter.BASIC_ISO_DATE);
			LocalDate nextLocalDate = startLocalDate.plusMonths(config.getType().num);
			nextLocalDate = nextLocalDate.with(TemporalAdjusters.firstDayOfMonth());
			long next = nextLocalDate.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(8)) * 1000;
			config.setNext(next);
			config.setNextDate(nextLocalDate.format(DateTimeFormatter.BASIC_ISO_DATE));
			config.setNeedCreated(true);
		}else {
			config.setNext(config.getStart());
			config.setStartDate(config.getStartDate());
			config.setNeedCreated(false);
		}
	}

	public static String generateStartDateRegex(Type type) {
		int num = 6;
		StringBuilder sb = new StringBuilder("^20\\d{2}(?:");
		IntStream.range(1, 12 / num + 1).forEach(n -> {
			n = num * (n - 1) + 1;
			if (n < 10) {
				sb.append("0");
			}
			sb.append(n).append("|");
		});
		sb.deleteCharAt(sb.length() - 1).append(")01$");
		return sb.toString();
	}
	
	private static boolean needCreate( long start ){
		long eightDaysMills = 691200000l;
		return (start - eightDaysMills) <= System.currentTimeMillis();
	}
}
