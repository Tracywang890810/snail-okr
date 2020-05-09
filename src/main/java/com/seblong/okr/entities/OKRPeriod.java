package com.seblong.okr.entities;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import lombok.Data;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Data
@Document(collection = "c_okr_periods")
@CompoundIndexes(value = { @CompoundIndex(name = "idx_enterprise_type_start", def = "{ 'enterpriseId' : 1, 'type' : 1, 'startDate' : 1 }"),
		@CompoundIndex(name = "idx_enterprise_year", def = "{ 'enterpriseId' : 1, 'year' : 1 }")
})
public class OKRPeriod {

	@JsonProperty(value = "unique")
	@JsonSerialize(using = ToStringSerializer.class)
	@Id
	private ObjectId id;

	// 企业id
	private String enterpriseId;

	// 周期类型
	@JsonSerialize(using = ToStringSerializer.class)
	private Type type;

	// 开始日期，格式为yyyyMMdd
	private String startDate;
	
	@JsonIgnore
	private String year;

	// 开始时间戳
	@JsonIgnore
	@Indexed
	private long start;

	// 结束时间戳
	@JsonIgnore
	private long end;

	@PersistenceConstructor
	public OKRPeriod(String enterpriseId, Type type, String startDate, String year, long start, long end) {
		this.enterpriseId = enterpriseId;
		this.type = type;
		this.startDate = startDate;
		this.year = year;
		this.start = start;
		this.end = end;
	}

	public static OKRPeriod build(String enterpriseId, Type type, String startDate, long start) {
		LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE);
		long end = startLocalDate.plusMonths(type.num - 1).with(TemporalAdjusters.lastDayOfMonth()).plusDays(1)
				.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(8));
		OKRPeriod okrPeriod = new OKRPeriod(enterpriseId, type, startDate,  startDate.substring(0, 5), start, end);
		return okrPeriod;
	}

	public static enum Type {
		MONTH(1), COUPLE(2), QUARTER(3), HALF(6), YEAR(12);
		public int num;

		private Type(int num) {
			this.num = num;
		}

	}
}
