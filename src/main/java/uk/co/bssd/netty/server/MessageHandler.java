package uk.co.bssd.netty.server;

public interface MessageHandler<S, T> {

	S onMessage(T message);
}