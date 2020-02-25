package com.seblong.okr.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Data;

@Data
@Document(collection = "c_okr_periods")
public class OKRPeriod {

	@JsonProperty(value = "unique")
	@JsonSerialize(using = ToStringSerializer.class)
	@Id
	private ObjectId id;

	@JsonSerialize(using = ToStringSerializer.class)
	private Type type;

	@Indexed
	private int year;

	private int rank;

	@JsonIgnore
	private long start;

	@JsonIgnore
	private long end;

	@PersistenceConstructor
	public OKRPeriod(Type type, int year, int rank, long start, long end) {
		this.type = type;
		this.year = year;
		this.rank = rank;
		this.start = start;
		this.end = end;
	}

	public static List<OKRPeriod> build(int year) {
		List<OKRPeriod> periods = new ArrayList<OKRPeriod>(8);
		IntStream.range(1, 5).forEach(rank -> {
			periods.add(new OKRPeriod(Type.QUARTER, year, rank, calculateStart(year, rank, Type.QUARTER),
					calculateEnd(year, rank, Type.QUARTER)));
		});
		periods.add(new OKRPeriod(Type.YEAR, year, 0, calculateStart(year, 0, Type.QUARTER),
				calculateEnd(year, 0, Type.QUARTER)));
		return periods;
	}

	private static long calculateStart(int year, int rank, Type type) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		int month = 0;
		if (Type.QUARTER.equals(type)) {
			month = 3 * (rank - 1) + 1;
		} else {
			month = 1;
		}
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTimeInMillis();
	}

	private static long calculateEnd(int year, int rank, Type type) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		int month = 0;
		if (Type.QUARTER.equals(type)) {
			month = 3 * (rank - 1) + 4;
		} else {
			calendar.add(Calendar.YEAR, 1);
			month = 1;
		}
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTimeInMillis();

	}

	public static enum Type {
		QUARTER, YEAR
	}
}
