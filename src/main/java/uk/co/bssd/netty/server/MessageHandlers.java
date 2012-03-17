package uk.co.bssd.netty.server;

import java.util.HashMap;
import java.util.Map;

public class MessageHandlers {

	private final Map<Class<?>, MessageHandler<?>> handlers;

	public MessageHandlers() {
		this.handlers = new HashMap<Class<?>, MessageHandler<?>>();
	}

	public void add(Class<?> messageType, MessageHandler<?> handler) {
		this.handlers.put(messageType, handler);
	}

	public MessageHandler<?> forType(Class<?> clazz) {
		return this.handlers.get(clazz);
	}
}