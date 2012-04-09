package uk.co.bssd.netty.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import uk.co.bssd.netty.dto.SynchronousResponse;

public class SynchronousMessageCollector {

	private final Map<UUID, SynchronousResponse> responses;
	
	public SynchronousMessageCollector() {
		this.responses = new HashMap<UUID, SynchronousResponse>();
	}
	
	public void onMessage(SynchronousResponse response) {
		this.responses.put(response.correlationId(), response);
	}
	
	public SynchronousResponse awaitResponse(UUID correlationId, long timeout) {
		
		long expiryTime = System.currentTimeMillis() + timeout;
		
		while (System.currentTimeMillis() < expiryTime) {
			if (this.responses.containsKey(correlationId)) {
				return this.responses.remove(correlationId);
			}
		}
		
		throw new TimeoutException("Timeout waiting for response with correlation id [" + correlationId + "]");
	}
}