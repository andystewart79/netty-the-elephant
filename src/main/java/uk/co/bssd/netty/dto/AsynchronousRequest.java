package uk.co.bssd.netty.dto;

import java.io.Serializable;
import java.util.UUID;

public class AsynchronousRequest implements Serializable{

	private static final long serialVersionUID = 1L;

	private final UUID messageId;
	
	private final Serializable payload;
	
	public AsynchronousRequest(Serializable payload) {
		this.messageId = UUID.randomUUID();
		this.payload = payload;
	}
	
	public UUID messageId() {
		return this.messageId;
	}
	
	public Serializable payload() {
		return this.payload;
	}
}