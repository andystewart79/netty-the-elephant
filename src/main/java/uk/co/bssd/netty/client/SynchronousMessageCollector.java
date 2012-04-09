package uk.co.bssd.netty.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

import uk.co.bssd.netty.dto.SynchronousResponse;

public class SynchronousMessageCollector {

	private final Map<UUID, SynchronousResponse> responses;
	
	public SynchronousMessageCollector() {
		this.responses = new HashMap<UUID, SynchronousResponse>();
	}
	
	public void onMessage(SynchronousResponse response) {
		synchronized (this.responses) {
			this.responses.put(response.correlationId(), response);
			this.responses.notify();
		}
	}
	
	public SynchronousResponse awaitResponse(UUID correlationId, long timeout) {
		
		long expiryTime = nowInMs() + timeout;
		
		while (System.currentTimeMillis() < expiryTime) {
			synchronized (this.responses) {
				if (!hasResponse(correlationId)){
					long waitMs = expiryTime - nowInMs();
					try {
						this.responses.wait(waitMs);
					} catch (InterruptedException e) {
						return null;
					}
				}
				
				if (hasResponse(correlationId)) {
					return this.responses.remove(correlationId);
				} 
			}
		}
		
		throw new MessageTimeoutException("Timeout waiting for response with correlation id [" + correlationId + "]");
	}

	private boolean hasResponse(UUID correlationId) {
		return this.responses.containsKey(correlationId);
	}

	private long nowInMs() {
		return new DateTime().getMillis();
	}
}