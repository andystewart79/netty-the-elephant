package uk.co.bssd.netty.server;

import java.io.Serializable;

public class IllegalThreadStateExceptionThrowingHandler implements SynchronousMessageHandler<Serializable, Serializable>{

	@Override
	public Serializable onMessage(Serializable message) {
		throw new IllegalThreadStateException();
	}
}