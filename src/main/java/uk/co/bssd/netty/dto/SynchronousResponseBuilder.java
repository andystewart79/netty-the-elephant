package uk.co.bssd.netty.dto;

import java.io.Serializable;
import java.util.UUID;

public class SynchronousResponseBuilder {

	private final UUID correlationId;
	
	private SynchronousResult result;
	private Serializable payload;

	private SynchronousResponseBuilder(UUID correlationId) {
		this.correlationId = correlationId;
	}
	
	public static SynchronousResponseBuilder responseFor(SynchronousRequest request) {
		return new SynchronousResponseBuilder(request.correlationId());
	}
	
	public SynchronousResponseBuilder success(Serializable response) {
		this.result = SynchronousResult.SUCCESS;
		this.payload = response;
		return this;
	}
	
	public SynchronousResponseBuilder failure(RuntimeException e) {
		this.result = SynchronousResult.FAILURE;
		this.payload = e;
		return this;
	}
	
	public SynchronousResponse build() {
		return new SynchronousResponse(this.correlationId, this.result, this.payload);
	}
}