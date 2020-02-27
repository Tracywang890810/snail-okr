package com.seblong.okr.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Data;

@Document(collection = "c_okrs")
@CompoundIndex(name = "idx_user_period", def = "{ 'user' : 1, 'period' : 1 }")
@Data
public class OKR implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty(value = "unique")
	@JsonSerialize(using = ToStringSerializer.class)
	@Id
	private ObjectId id;

	private String user;

	private String period;

	private List<Objective> objectives;

	private long created;

	private long updated;
	
	@PersistenceConstructor
	public OKR(String user, String period, List<Objective> objectives, long created, long updated) {
		this.user = user;
		this.period = period;
		this.objectives = objectives;
		this.created = created;
		this.updated = updated;
	}

	public OKR(String user, String period) {
		this.user = user;
		this.period = period;
		this.created = System.currentTimeMillis();
		this.updated = created;
		this.objectives = new ArrayList<OKR.Objective>();
	}
	
	public void addObjective(Objective objective) {
		this.objectives.add(objective);
	}

	@Data
	public static class Objective implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@JsonProperty(value = "unique")
		@JsonSerialize(using = ToStringSerializer.class)
		private ObjectId id;

		private String title;

		private double score;

		private long estimate;

		private double confidence;

		private List<KeyResult> keyResults;

		private String progress;

		private long created;

		private long updated;

		public Objective(String title, long estimate, double confidence) {
			this.id = new ObjectId();
			this.title = title;
			this.estimate = estimate;
			this.confidence = confidence;
			this.created = System.currentTimeMillis();
			this.updated = created;
			this.progress = "";
			this.keyResults = new ArrayList<OKR.KeyResult>();
		}

		public Objective() {
		}
		
		public void addKeyResult(KeyResult keyResult) {
			this.keyResults.add(keyResult);
		}
		
		public void calculateScore() {
			if( !CollectionUtils.isEmpty(keyResults) ) {
				this.score = 0;
				for( KeyResult keyResult : keyResults ) {
					this.score += keyResult.getScore() * keyResult.getWeight();
				}
			}
		}
		
	}

	@Data
	public static class KeyResult implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@JsonProperty(value = "unique")
		@JsonSerialize(using = ToStringSerializer.class)
		private ObjectId id;

		private String title;

		private double score;

		private long estimate;

		private double confidence;
		
		private double weight;
		
		private long created;

		private long updated;

		public KeyResult(String title, long estimate, double confidence, double weight) {
			this.id = new ObjectId();
			this.title = title;
			this.estimate = estimate;
			this.confidence = confidence;
			this.weight = weight;
			this.created = System.currentTimeMillis();
			this.updated = created;
		}

		public KeyResult() {
		}
		
		
	}

}
