package com.seblong.okr.resource;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class StandardListResource<T> extends StandardRestResource {

	private List<T> result;

	public StandardListResource() {
		super( 200, "OK" );
		this.result = new ArrayList<T>();
	}

	public StandardListResource( List<T> result ) {
		super( 200, "OK" );
		this.result = result;
	}
	
	public void addAttribute( T attr ) {
		this.result.add( attr );
	}

	public List<T> getResult() {
		return result;
	}

	public void setResult( List<T> result ) {
		this.result = result;
	}

}
