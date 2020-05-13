package com.seblong.okr.entities;

import lombok.Data;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "c_okr_history")
@CompoundIndex(name = "idx_date_user_period", def = "{ 'user' : 1, 'period' : 1, 'date' : -1 }")
@Data
public class OKRHistory {

	@Id
	private ObjectId id;
	
	//yyyyMMdd
	private String date;
	
	private String user;
	
	@Indexed
	private String enterpriseId;
	
	private String period;
	
	@Reference
	private OKR okr;
	
	private long created;

	@PersistenceConstructor
	public OKRHistory(String date, String user, String enterpriseId, String period, OKR okr, long created) {
		this.date = date;
		this.user = user;
		this.enterpriseId = enterpriseId;
		this.period = period;
		this.okr = okr;
		this.created = created;
	}

	public OKRHistory(String date, OKR okr) {
		this.date = date;
		this.okr = okr;
		this.user = okr.getUser();
		this.enterpriseId = okr.getEnterpriseId();
		this.period = okr.getPeriod();
		this.created = System.currentTimeMillis();
	}
	
	public void update(OKR okr) {
		this.okr = okr;
		this.created = System.currentTimeMillis();
	}
	
}
