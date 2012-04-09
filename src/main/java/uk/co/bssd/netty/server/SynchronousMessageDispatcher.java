package uk.co.bssd.netty.server;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;

import org.jboss.netty.channel.Channel;

import uk.co.bssd.netty.dto.SynchronousRequest;

public class SynchronousMessageDispatcher {

	private final ExecutorService executor;
	private final SynchronousMessageHandlers<Serializable,  Serializable> messageHandlers;
	
	public SynchronousMessageDispatcher(ExecutorService executor) {
		this.executor = executor;
		this.messageHandlers = new SynchronousMessageHandlers<Serializable,Serializable>();
	}
	
	@SuppressWarnings("unchecked")
	public void registerHandler(Class<? extends Serializable> clazz,
			SynchronousMessageHandler<? extends Serializable, ? extends Serializable> handler) {
		this.messageHandlers.add(clazz,  (SynchronousMessageHandler<Serializable, Serializable>)handler);
	}
	
	public void dispatch(SynchronousRequest request, Channel responseChannel) {
		SynchronousMessageHandler<Serializable, Serializable> handler = this.messageHandlers.forType(request.payload().getClass());
		SynchronousMessageTask task = new SynchronousMessageTask(handler, request, responseChannel);
		this.executor.submit(task);
	}
}