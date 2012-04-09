package uk.co.bssd.netty.dto;

import java.io.Serializable;
import java.util.UUID;

public class SynchronousResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private final UUID correlationId;
	private final SynchronousResult result;
	private final Serializable payload;

	public SynchronousResponse(UUID correlationId, SynchronousResult result, Serializable payload) {
		this.correlationId = correlationId;
		this.result = result;
		this.payload = payload;
	}
	
	public UUID correlationId() {
		return this.correlationId;
	}
	
	public SynchronousResult result() {
		return this.result;
	}
	
	public boolean success() {
		return result() == SynchronousResult.SUCCESS;
	}
	
	public Serializable payload() {
		return this.payload;
	}
}