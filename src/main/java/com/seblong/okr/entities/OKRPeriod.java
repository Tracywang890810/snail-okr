package com.seblong.okr.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Data;

@Data
@Document(collection = "c_okr_periods")
public class OKRPeriod {

	@JsonProperty(value = "unique")
	@Id
	private ObjectId id;
	
	@JsonSerialize(using = ToStringSerializer.class)
	private Type type;
	
	@Indexed
	private int year;
	
	private int rank;

	@PersistenceConstructor
	public OKRPeriod(Type type, int year, int rank) {
		this.type = type;
		this.year = year;
		this.rank = rank;
	}

	public static List<OKRPeriod> build( int year ){
		List<OKRPeriod> periods = new ArrayList<OKRPeriod>(8);
		IntStream.range(1, 5).forEach(rank ->{
			periods.add(new OKRPeriod(Type.QUARTER, year, rank));
		});
		periods.add(new OKRPeriod(Type.YEAR, year, 0));
		return periods;
	}
	
	public static enum Type {
		QUARTER, YEAR
	}
	
}
