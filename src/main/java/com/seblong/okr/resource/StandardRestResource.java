package com.seblong.okr.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class StandardRestResource {

	protected int status;
	protected String message;

	public StandardRestResource( int status, String message ) {
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus( int status ) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage( String message ) {
		this.message = message;
	}

	public static StandardRestResource fail(String msg) {
        return new StandardRestResource(400, msg);
    }
}
