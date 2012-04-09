package uk.co.bssd.netty.server;

import java.util.HashMap;
import java.util.Map;

public class SynchronousMessageHandlers<REQ, RESP> {

	private final SynchronousMessageHandler<REQ, RESP> unknownTypeHandler;
	
	private final Map<Class<? extends REQ>, SynchronousMessageHandler<REQ, RESP>> handlers;

	public SynchronousMessageHandlers() {
		this.unknownTypeHandler = new UnknownSynchronousRequestHandler<REQ, RESP>();
		this.handlers = new HashMap<Class<? extends REQ>, SynchronousMessageHandler<REQ, RESP>>();
	}

	public void add(Class<? extends REQ> messageType, SynchronousMessageHandler<REQ, RESP> handler) {
		this.handlers.put(messageType, handler);
	}

	public SynchronousMessageHandler<REQ, RESP> forType(Class<? extends REQ> clazz) {
		if (this.handlers.containsKey(clazz)) {
			return this.handlers.get(clazz);
		}
		return this.unknownTypeHandler;
	}
}