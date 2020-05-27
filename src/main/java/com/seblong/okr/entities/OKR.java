package com.seblong.okr.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Data;

@Document(collection = "c_okrs")
@CompoundIndex(name = "idx_user_period", def = "{ 'user' : 1, 'period' : 1 }", unique = true)
@Data
public class OKR implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty(value = "unique")
	@JsonSerialize(using = ToStringSerializer.class)
	@Id
	private ObjectId id;

	@Indexed
	private String enterpriseId;
	
	private String user;

	private String period;

	private List<Objective> objectives;

	private long created;

	private long updated;

	@PersistenceConstructor
	public OKR(String user, String enterpriseId, String period, List<Objective> objectives, long created, long updated) {
		this.user = user;
		this.enterpriseId = enterpriseId;
		this.period = period;
		this.objectives = objectives;
		this.created = created;
		this.updated = updated;
	}
	
	public OKR(String user, String period, String enterpriseId) {
		this(user, period);
		this.enterpriseId =enterpriseId;
	}

	public OKR(String user, String period) {
		this.user = user;
		this.period = period;
		this.created = System.currentTimeMillis();
		this.updated = created;
		this.objectives = new ArrayList<OKR.Objective>();
	}

	public boolean rank(int oldRank, int newRank) {
		if( objectives.size() > 0 ){
			int maxIndex = this.objectives.size() - 1;
			if( oldRank <= maxIndex && newRank <= maxIndex ) {
				this.objectives.add(newRank, this.objectives.remove(oldRank));
				return true;
			}
		}
		return false;
	}
	
	public void addObjective(Objective objective) {
		this.objectives.add(objective);
	}

	public Objective findObjective(ObjectId objectiveId) {
		if (objectives.size() > 0) {
			for (int i = 0; i < objectives.size(); i++) {
				if (objectives.get(i).getId().equals(objectiveId)) {
					return objectives.get(i);
				}
			}
		}
		return null;
	}
	
	public Objective findAndRemoveOBjective(ObjectId objectiveId) {
		if (objectives.size() > 0) {
			int i = 0;
			boolean find = false;
			for (; i < objectives.size(); i++) {
				if (objectives.get(i).getId().equals(objectiveId)) {
					find = true;
					break;
				}

			}
			if (find)
				return objectives.remove(i);
		}
		return null;
	}

	@Data
	public static class Objective implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@JsonProperty(value = "unique")
		@JsonSerialize(using = ToStringSerializer.class)
		private ObjectId id;

		private String title;

		private int score;

		private long estimate;

		private int confidence;

		private List<KeyResult> keyResults;

		private String progress;

		private long created;

		private long updated;
		
		public Objective(String title) {
			this(title, -1, -1);
		}

		public Objective(String title, long estimate, int confidence) {
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
			this.updated = System.currentTimeMillis();
		}

		public int getKeyResultSize() {
			return this.keyResults.size();
		}

		public boolean rank(int oldRank, int newRank) {
			if( this.keyResults.size() > 0 ) {
				int maxIndex = this.keyResults.size() - 1;
				if( oldRank <= maxIndex && newRank <= maxIndex ) {
					this.keyResults.add(newRank,  this.keyResults.remove(oldRank));
					this.updated = System.currentTimeMillis();
					return true;
				}
			}
			return false;
		}

		public KeyResult findKeyResult(ObjectId keyresultId) {

			if (keyResults.size() > 0) {
				for (int i = 0; i < keyResults.size(); i++) {
					if (keyResults.get(i).getId().equals(keyresultId)) {
						return keyResults.get(i);
					}
				}
			}
			return null;

		}

		public KeyResult findAndRemoveKeyResult(ObjectId keyresultId) {
			if (keyResults.size() > 0) {
				int i = 0;
				boolean find = false;
				for (; i < keyResults.size(); i++) {
					if (keyResults.get(i).getId().equals(keyresultId)) {
						find = true;
						break;
					}

				}
				if (find)
					return keyResults.remove(i);
			}
			return null;
		}

		public KeyResult scoreKeyResult(ObjectId keyresultId, int socre){
			KeyResult keyResult = findKeyResult(keyresultId);
			if( keyResult != null ){
				keyResult.setScore(socre);
				this.updated = System.currentTimeMillis();
				calculateScore();
			}
			return keyResult;
		}
		
		private void calculateScore() {
			if (!CollectionUtils.isEmpty(keyResults)) {
				this.score = 0;
				for (KeyResult keyResult : keyResults) {
					this.score += keyResult.getScore() * keyResult.getWeight();
				}
				this.score = Math.round(this.score / 100f);
			}
		}

	}

	@Data
	public static class KeyResult implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@JsonProperty(value = "unique")
		@JsonSerialize(using = ToStringSerializer.class)
		private ObjectId id;

		private String title;

		private int progress;

		private int score;

		private long estimate;

		private int confidence;

		private int weight;

		private long created;

		private long updated;

		public KeyResult(String title, long estimate, int confidence, int weight, int progress) {
			this.id = new ObjectId();
			this.title = title;
			this.estimate = estimate;
			this.confidence = confidence;
			this.weight = weight;
			this.progress = progress;
			this.created = System.currentTimeMillis();
			this.updated = created;
		}

		public KeyResult(String title) {
			this(title, -1, -1, -1, 0);
		}
		
		public KeyResult() {
		}

	}

}
