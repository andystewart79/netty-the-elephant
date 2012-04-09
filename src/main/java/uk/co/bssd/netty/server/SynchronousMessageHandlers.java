package uk.co.bssd.netty.server;

import java.util.HashMap;
import java.util.Map;

public class SynchronousMessageHandlers<REQ, RESP> {

	private final Map<Class<? extends REQ>, SynchronousMessageHandler<REQ, RESP>> handlers;

	public SynchronousMessageHandlers() {
		this.handlers = new HashMap<Class<? extends REQ>, SynchronousMessageHandler<REQ, RESP>>();
	}

	public void add(Class<? extends REQ> messageType, SynchronousMessageHandler<REQ, RESP> handler) {
		this.handlers.put(messageType, handler);
	}

	public SynchronousMessageHandler<REQ, RESP> forType(Class<? extends REQ> clazz) {
		return this.handlers.get(clazz);
	}
}