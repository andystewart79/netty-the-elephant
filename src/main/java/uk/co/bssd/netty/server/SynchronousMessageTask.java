package uk.co.bssd.netty.server;

import java.io.Serializable;

import org.jboss.netty.channel.Channel;

import uk.co.bssd.netty.dto.SynchronousRequest;
import uk.co.bssd.netty.dto.SynchronousResponseBuilder;

public class SynchronousMessageTask implements Runnable {

	private final SynchronousMessageHandler<Serializable, Serializable> handler;
	private final SynchronousRequest request;
	private final Channel responseChannel;

	public SynchronousMessageTask(SynchronousMessageHandler<Serializable, Serializable> handler,
			SynchronousRequest request, Channel responseChannel) {
		this.handler = handler;
		this.request = request;
		this.responseChannel = responseChannel;
	}

	@Override
	public void run() {
		Serializable payload = this.request.payload();
		
		SynchronousResponseBuilder responseBuilder = SynchronousResponseBuilder.responseFor(this.request);

		try {
			Serializable response = this.handler.onMessage(payload);
			responseBuilder.success(response);
		}
		catch (RuntimeException e) {
			responseBuilder.failure(e);
		}
		
		this.responseChannel.write(responseBuilder.build());
	}
}