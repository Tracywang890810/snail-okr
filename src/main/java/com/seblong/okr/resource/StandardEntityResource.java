package com.seblong.okr.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class StandardEntityResource<T> extends StandardRestResource {

	private T result;

	public StandardEntityResource( T res ) {
		super( 200, "OK" );
		this.result = res;
	}

	public T getResult() {
		return result;
	}

	public void setResult( T result ) {
		this.result = result;
	}
}
