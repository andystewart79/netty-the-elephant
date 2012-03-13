package uk.co.bssd.netty.dto;

import java.io.Serializable;

public class SimpleResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String payload;
	
	public SimpleResponse(String payload) {
		this.payload = payload;
	}
	
	public String payload() {
		return this.payload;
	}
}