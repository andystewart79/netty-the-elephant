package uk.co.bssd.netty.server;

import java.io.Serializable;

import uk.co.bssd.netty.dto.AsynchronousRequest;

public class AsynchronousMessageTask implements Runnable {

	private final AsynchronousMessageHandler<Serializable> handler;
	private final AsynchronousRequest request;

	public AsynchronousMessageTask(AsynchronousMessageHandler<Serializable> handler,
			AsynchronousRequest request) {
		this.handler = handler;
		this.request = request;
	}

	@Override
	public void run() {
		Serializable payload = this.request.payload();
		
		try {
			this.handler.onMessage(payload);
		}
		catch (RuntimeException e) {
			// TODO log exception
		}
	}
}