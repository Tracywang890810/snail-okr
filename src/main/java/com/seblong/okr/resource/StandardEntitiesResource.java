package com.seblong.okr.resource;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class StandardEntitiesResource<T> extends StandardRestResource {

	public class EntitiesResource{
		private long total;
		private boolean hasPrevious = false;
		private boolean hasNext = false;
		private List<T> entities;

		public EntitiesResource( long total, List<T> entities, boolean hasPrevious, boolean hasNext ) {
			this.entities = entities;
			this.total = total;
			this.hasNext = hasNext;
			this.hasPrevious = hasPrevious;
		}

		public long getTotal() {
			return total;
		}

		public void setTotal( long total ) {
			this.total = total;
		}

		public List<T> getEntities() {
			return entities;
		}

		public void setEntities( List<T> entities ) {
			this.entities = entities;
		}

		public boolean isHasPrevious() {
			return hasPrevious;
		}

		public void setHasPrevious( boolean hasPrevious ) {
			this.hasPrevious = hasPrevious;
		}

		public boolean isHasNext() {
			return hasNext;
		}

		public void setHasNext( boolean hasNext ) {
			this.hasNext = hasNext;
		}
	}

	private EntitiesResource result;

	public StandardEntitiesResource( List<T> entities, long total, boolean hasPrevious, boolean hasNext ) {
		super( 200, "OK" );
		result = new EntitiesResource( total, entities, hasPrevious, hasNext );
	}
	
	public EntitiesResource getResult() {
		return result;
	}

	public void setResult( EntitiesResource result ) {
		this.result = result;
	}
	
	public static <T> StandardEntitiesResource<T> empty(){
		return new StandardEntitiesResource<T>(Collections.emptyList(), 0, false, false);
	}

}
