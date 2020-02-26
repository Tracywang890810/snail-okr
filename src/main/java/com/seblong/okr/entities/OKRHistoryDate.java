package com.seblong.okr.entities;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "c_okr_history_date")
@CompoundIndex(name = "idx_user_period", def = "{ 'user' : 1, 'period' : 1 }")
@Data
public class OKRHistoryDate {

	@Id
	private ObjectId id;
	
	private String user;
	
	private String period;
	
	private List<String> dates;

	@PersistenceConstructor
	public OKRHistoryDate(String user, String period, List<String> dates) {
		this.user = user;
		this.period = period;
		this.dates = dates;
	}

	public OKRHistoryDate(String user, String period) {
		this.user = user;
		this.period = period;
		this.dates = new ArrayList<String>();
	}
	
	public boolean addDate(String date) {
		if( !dates.contains(date) ) {
			dates.add(date);
			return true;
		}
		return false;
	}
	
}
