package uk.co.bssd.netty.server;

import java.util.HashMap;
import java.util.Map;

public class AsynchronousMessageHandlers<REQ> {

	private final Map<Class<? extends REQ>, AsynchronousMessageHandler<REQ>> handlers;

	public AsynchronousMessageHandlers() {
		this.handlers = new HashMap<Class<? extends REQ>, AsynchronousMessageHandler<REQ>>();
	}

	public void add(Class<? extends REQ> messageType, AsynchronousMessageHandler<REQ> handler) {
		this.handlers.put(messageType, handler);
	}

	public AsynchronousMessageHandler<REQ> forType(Class<? extends REQ> clazz) {
		return this.handlers.get(clazz);
	}
}