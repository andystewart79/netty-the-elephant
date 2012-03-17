package uk.co.bssd.netty.server;

public interface MessageHandler<T> {

	void onMessage(T message);
}