package uk.co.bssd.netty.dto;

import java.io.Serializable;
import java.util.UUID;

public class SynchronousRequest implements Serializable{

	private static final long serialVersionUID = 1L;

	private final UUID correlationId;
	
	private final Serializable payload;
	
	public SynchronousRequest(Serializable payload) {
		this.correlationId = UUID.randomUUID();
		this.payload = payload;
	}
	
	public UUID correlationId() {
		return this.correlationId;
	}
	
	public Serializable payload() {
		return this.payload;
	}
}